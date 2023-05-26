library ieee;
use ieee.std_logic_1164.all;

entity brojke is
    port (
        me_left, me_right, me_up, me_down, me_center: in std_logic; -- ulazi ULX2S
        me_code: out std_logic_vector(7 downto 0) -- izlaz
    );
end brojke;

architecture arch_br of brojke is 
    signal me_ulazi: std_logic_vector(4 downto 0);
    
begin

    me_ulazi <= me_down & me_left & me_center & me_up & me_right;
    
    with me_ulazi select
    
    me_code <=
        "00000000" when "00000",
	"00000000" when "10000",
	"00110011" when "01000",
	"00110110" when "00100",
	"00110101" when "00010",
	"00110001" when "00001",
	"00110100" when "11000",
	"00110000" when "10100",
	"00111000" when "10010",
	"00111001" when "10001",
	"--------" when others ;
	
end arch_br;