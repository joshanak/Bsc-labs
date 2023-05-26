# ! /usr/bin/env python3


from Crypto.Random import get_random_bytes
from Crypto.Cipher import AES
from Crypto.Protocol.KDF import scrypt
import sys
import os


#  inicijalizira bazu podataka
def init(masterpassword):
	mp = masterpassword
	open(r"baza_podataka.txt", 'wb')


# UPUTE
# spaja adresu i sifru iz argumenata(izmedu |)  i dobivam zapis
# zatim dekriptiram cijelu datoteku da dobijem sve zapise
# ako je datoteka na neki nacin komprimirana prekidam i ispisujem gresku
# trenutno stvoreni zapis stavljam na kraj
# ako vec postoji u datoteci unesena adresa zamijenim staru sifru sa novom
# to nazivam data i pocinjem proces enkripcije
# stvaram salt pomocu get_random_bytes i s njime i masterpass radim key =  scrypt(mp, salt)
# zatim tim ključem radim cipher te njime enkriptiram data
# takoder stvaram tag radi verifikacije
# redom pisem sve u datoteku jednim za drugim (salt, nonce, enkriptiranu_data, tag) u bajtovima
# nonce je dio cipher strukture (cipher.nonce)
def myencode(masterpassword, address, password):
	data_s = mydecode(masterpassword)  # dobijem sve prosle zapise
	if data_s == "E":
		print("Dirana je datoteka - prekidam")
	fwrite = open(r"baza_podataka.txt", "wb")
	test = searchdataforadr(data_s, address)  # test = [adresa, sifra]
	if test is not None:
		password = password + "\n"
		data_s = data_s.replace(test[1], password)
	else:
		record_s = address + "|" + password + "\n"  # address|password
		data_s = data_s + record_s  # dodajem na kraj
	data_b = data_s.encode("utf-8")
	salt_b = get_random_bytes(32)  # generiraj salt - 32 bajtova po konvneciji
	key_b = scrypt(masterpassword, salt_b, key_len=32, N=2 ** 17, r=8, p=1)  # KDF
	cipher = AES.new(key_b, AES.MODE_GCM)  # cypher objekt za enkripciju
	encrypted_data_b = cipher.encrypt(data_b)  # enkriptiraj datoteku
	tag_b = cipher.digest()  # stvori MAC tag - 16 bajtova po konvenciji
	fwrite.write(salt_b)
	fwrite.write(cipher.nonce)
	fwrite.write(encrypted_data_b)
	fwrite.write(tag_b)
	fwrite.close()


# UPUTE
# iz datoteke procitam redom solt, nonce(iv), enkriptirana_data i tag
# rekreiram key pomocu masterpass i salta
# na temelju tog kljuca i nonce(iv) radim cipher
# cipherom dekriptiram procitanu datoteku te verificiram tag
# ako je datoteka dirana cipher.digest baca error
# takoder ako je datoteka dirana nece se moci utf-8 dekodirati pa ce i tu baciti error
# vracam dekriptiranu datoteku kao string
def mydecode(masterpassword):
	fread = open(r"baza_podataka.txt", "rb")
	length = os.stat("baza_podataka.txt").st_size
	if length == 0:
		return ""
	salt_b = fread.read(32)  # prvih 32 salt
	nonce_b = fread.read(16)  # sljedecih 16 je nonce ili iv
	data_b = fread.read(length - 64)  # podatak = len(cijeli_unos) - 64(salt + iv + tag)
	tag_b = fread.read(16)  # ostatak je tag
	key_b = scrypt(masterpassword, salt_b, key_len=32, N=2 ** 17, r=8, p=1)  # rekonstruiraj ključ
	cipher = AES.new(key_b, AES.MODE_GCM, nonce=nonce_b)
	decrypted_data_b = cipher.decrypt(data_b)
	try:
		decrypted_data_s = decrypted_data_b.decode("utf-8")
		cipher.verify(tag_b)
	except (UnicodeDecodeError, ValueError) as e:
		return "E"
	return decrypted_data_s


# UPUTE
# splita sve zapise u data na temelju "\n" i dobiva listu zapisa
# prolazi redom zapise te svaki splita na temelju "|" i dobiva listu
# provjerava je li jednaka trazenoj adresi te vraca objekt [adresa, sifra]
# inace ne vraca nista
def searchdataforadr(data_s, searchedadr_s):
	records_s = data_s.splitlines()
	for line_s in records_s:
		zapis_s = line_s.split("|")
		address_s = zapis_s[0]
		if address_s == searchedadr_s:
			return zapis_s
	return None


if len(sys.argv) == 3:
	if sys.argv[1] == 'init':
		init(sys.argv[2])

if len(sys.argv) == 4:
	if sys.argv[1] == 'get':
		decrypted_data = mydecode(sys.argv[2])
		if decrypted_data == "E":
			print("Dirana je datoteka")
		else:
			result = searchdataforadr(decrypted_data, sys.argv[3])
			if result is None:
				print("Nema trazene adrese")
			else:
				print("Trazena sifra je " + result[1])

if len(sys.argv) == 5:
	if sys.argv[1] == 'put':
		myencode(sys.argv[2], sys.argv[3], sys.argv[4])
