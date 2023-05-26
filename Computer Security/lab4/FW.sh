#! /bin/sh

IPT=/sbin/iptables

$IPT -P INPUT DROP
$IPT -P OUTPUT DROP
$IPT -P FORWARD DROP

$IPT -F INPUT
$IPT -F OUTPUT
$IPT -F FORWARD

$IPT -A INPUT   -m state --state ESTABLISHED,RELATED -j ACCEPT
$IPT -A OUTPUT  -m state --state ESTABLISHED,RELATED -j ACCEPT
$IPT -A FORWARD -m state --state ESTABLISHED,RELATED -j ACCEPT

# 
# za potrebe testiranja dozvoljen je ICMP (ping i sve ostalo)
#
$IPT -A INPUT   -p icmp -j ACCEPT
$IPT -A FORWARD -p icmp -j ACCEPT
$IPT -A OUTPUT  -p icmp -j ACCEPT


# ================ Dodajte ili modificirajte pravila na oznacenim mjestima #
# "anti spoofing" (eth0)
#
$IPT -A INPUT   -i eth0 -s 127.0.0.0/8  -j DROP
$IPT -A FORWARD -i eth0 -s 127.0.0.0/8  -j DROP
$IPT -A INPUT   -i eth0 -s 10.0.0.10/24  -j DROP
$IPT -A FORWARD -i eth0 -s 10.0.0.10/24  -j DROP
#
# <--- Dodajte ili modificirajte pravila 	
#
# racunala iz lokalne mreze (LAN) imaju neograniceni pristup posluziteljima u DMZ i Internetu
#
# <--- Dodajte ili modificirajte pravila
$IPT -A FORWARD -s 10.0.0.1/2 -j ACCEPT

#
# iz vanjske mreze (Interneta) dozvoljen je pristup posluzitelju server u DMZ korištenjem 
# protokola SSH (tcp port 22) i DNS (udp i tcp port 53)
#
# <--- Dodajte pravila
$IPT -A FORWARD -i eth0 -d 203.0.113.10 -p tcp --dport 22 -j ACCEPT
$IPT -A FORWARD -i eth0 -d eth2 -p tcp --dport 53 -j ACCEPT
$IPT -A FORWARD -i 192.0.2.21 -d eth2 -p udp --dport 53 -j ACCEPT
#
# pristup iz vanjske mreze i DMZ u lokalnu LAN mrezu je zabranjen, dozvoljen je samo 
# pristup posluzitelju host (u LAN-u) s posluzitelja server (u DMZ) korištenjem protokola SSH
#
# <--- Dodajte pravila
$IPT -A FORWARD -p tcp -s 203.0.113.10 -d 10.0.0.11 --dport 22 -j ACCEPT
#
# s posluzitelja server je dozvoljen pristup DNS posluziteljima u Internetu (UDP i TCP port 53)
#
# <--- Dodajte pravila
$IPT -A FORWARD -i eth2 -p udp -s 203.0.113.10 --dport 53 -j ACCEPT
$IPT -A FORWARD -i eth2 -p tcp -s 203.0.113.10 --dport 53 -j ACCEPT
#
# SSH pristup vatrozidu firewall je dozvoljen samo s računala admin (LAN)
#
# <--- Dodajte pravila
$IPT -A INPUT -s 10.0.0.10 -p tcp --dport 22 -j ACCEPT