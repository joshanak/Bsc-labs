library ieee;
use ieee.std_logic_1164.all;
use ieee.std_logic_unsigned.all;
use ieee.std_logic_arith.all;

entity alu is
	generic (
	   sirina_io: integer := 4
	);
	port (
	   ALU_A, ALU_B: in std_logic_vector((sirina_io - 1) downto 0);
	   ALUOp: in std_logic_vector(2 downto 0);
	   ALU_Z: out std_logic_vector((sirina_io - 1) downto 0)
	);
end alu;
	   
architecture alu_arch of alu is
	signal mul: std_logic_vector(((sirina_io * 2) - 1) downto 0);

begin
	mul <= ALU_A * ALU_B;
	
	with ALUOp select 
	ALU_Z <= 
	    mul((sirina_io - 1) downto 0)        when "000",
	    NOT (ALU_A OR ALU_B) 		 when "001",
	    ALU_A - ALU_B        		 when "010",
	    ALU_A AND ALU_B      		 when "011",
	    SHR(ALU_A, ALU_B)    		 when "100",
	    ALU_A + ALU_B        		 when "101",
	    ALU_A OR ALU_B       		 when "110",
	    ALU_A XOR ALU_B      		 when "111";
 
end alu_arch;
