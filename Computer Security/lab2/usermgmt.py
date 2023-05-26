from Crypto.Random import get_random_bytes
from Crypto.Protocol.KDF import scrypt
import sys
import os
from getpass import getpass
#  u datoteci su mi spremljeni u bajtovima username i password, izmedu je | a na kraju \n


def manage(function_name, username):
    fread = open(r"bazapodatak.txt", "r+")
    check_for_user = 0
    record = ""
    allusers = ""
    while True:
        line = fread.readline()
        allusers = allusers + line
        if line.startswith(username):
            check_for_user = 1
            record = line
        if not line:
            break
    if function_name == "add":
        if check_for_user == 1:  # ako vec postoji korisnik
            print("User already present in database")
            return
        else:  # ako ne postoji korisnik dodaj ga
            print("Password: ")
            password = getpass()
            print("Repeat password")
            if password != getpass():
                print("User add failed. Password mismatch")
                return
            else:
                salt_b = get_random_bytes(32)  # generiraj salt
                key_b = scrypt(password, salt_b, key_len=32, N=2 ** 17, r=8, p=1)  # KDF
                data = username + "|" + salt_b.hex() + key_b.hex() + "\n"  # napravi podatak za spremanje
                allusers = allusers + data
                print(username + " successfuly added.")
                fwrite = open(r"bazapodatak.txt", "w")
                fwrite.write(allusers)  # zapisi u bazu
                return
    if check_for_user == 0:  # za sve ostale naredbe potreban user u bazi
        print("Cannot execute function -> username not in database")
        return
    if function_name == "forcepass":
        password = record.split("|")[1]
        data = username + "0" + "|" + password + "\n"  # napravi podatak za spremanje
        allusers = allusers.replace(record.strip(), data)
        print("User will be requested to change password on next login")
        fwrite = open(r"bazapodatak.txt", "w")
        fwrite.write(allusers)  # zapisi u bazu
        return
    if function_name == "del":
        allusers = allusers.replace(record, "")
        print(username + " successfuly removed.")
        fwrite = open(r"bazapodatak.txt", "w")
        fwrite.write(allusers)
        return
    if function_name == "passwd":
        print("Password: ")
        password = getpass()
        print("Repeat password")
        if password != getpass():
            print("User add failed. Password mismatch")
            return
        else:
            salt_b = get_random_bytes(32)  # generiraj salt
            key_b = scrypt(password, salt_b, key_len=32, N=2 ** 17, r=8, p=1)  # KDF
            data = username + "|" + salt_b.hex() + key_b.hex() + "\n"  # napravi podatak za spremanje
            allusers = allusers.replace(record.strip(), data)
            fwrite = open(r"bazapodatak.txt", "w")
            fwrite.write(allusers)  # zapisi u bazu
            print("Password change successful")
            return


# Press the green button in the gutter to run the script.
if __name__ == '__main__':
    manage(sys.argv[1].strip(), sys.argv[2].strip())
