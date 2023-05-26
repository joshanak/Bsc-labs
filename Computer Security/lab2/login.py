from Crypto.Random import get_random_bytes
from Crypto.Protocol.KDF import scrypt
import sys
from getpass import getpass


# spremit cu u bazu kao username|salthashpass prvih 16 byteova salt
def login(username):
    fread = open(r"bazapodatak.txt", "r+")
    check_for_user = 0
    record = ""
    allusers = ""
    while True:
        line = fread.readline()
        allusers = allusers + line
        if line.startswith(username):
            check_for_user = 1
            record = line.strip()
        if not line:
            break

    if check_for_user == 0:
        print("Nije naden")
        for x in range(3):
            print("Password: ")
            print("Username or password incorrect")
        return
    data = record.split("|")
    hash_pass_full = data[1]
    username = data[0]
    salt_b = bytes.fromhex(hash_pass_full[:64])
    password_original_b = bytes.fromhex(hash_pass_full[64:])
    changepass = 0
    if username.endswith("0"):
        changepass = 1
    for i in range(3):
        print("Password: ")
        password = input()
        password_new_b = scrypt(password, salt_b, key_len=32, N=2 ** 17, r=8, p=1)  # hashing
        if password_new_b == password_original_b:
            if changepass == 1:
                print("New password: ")
                password = input()
                print("Repeat new password")
                if password != input():
                    x = "bla"
                    print("Username or password incorrect.")
                    continue
                else:
                    new_salt_b = get_random_bytes(32)  # generiraj salt
                    key_b = scrypt(password, new_salt_b, key_len=32, N=2 ** 17, r=8, p=1)  # KDF
                    username = username[:-1]
                    new_data = username + "|" + new_salt_b.hex() + key_b.hex() + "\n"  # napravi podatak za spremanje
                    allusers = allusers.replace(record, new_data)
                    print("Login successful")
                    fwrite = open(r"bazapodatak.txt", "w")
                    fwrite.write(allusers)  # zapisi u bazu
                    return
            else:
                print("Login successful.")
                return
        else:
            print("Username or password incorrect")

    fread.close()


# Press the green button in the gutter to run the script.
if __name__ == '__main__':
    login(sys.argv[1].strip())


