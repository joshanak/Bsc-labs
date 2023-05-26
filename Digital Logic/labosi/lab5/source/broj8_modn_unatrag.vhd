library ieee;
use ieee.std_logic_1164.all;
use ieee.std_logic_unsigned.all;

entity broj is
    port (
	clk_25m: in std_logic;
	btn_left, btn_right, btn_up: in std_logic;
	led: out std_logic_vector(7 downto 0)
    );
end broj;

architecture x of broj is
    signal R: std_logic_vector(7 downto 0);
    signal clk: std_logic;

begin
    
    upravljac: entity upravljac
    port map (
	Clk_key => btn_up,
	AddrA_key => '0',
	AddrB_key => '0',
	ALUOp_key => '0',
	clk_25m => clk_25m,
	AddrA => open,
	AddrB => open,
	ALUOp => open,
	Clk => clk
    );
    
    process(clk, btn_right, R)
    begin
        if rising_edge(clk) then
	        if btn_left = '1' then
	            R <= "01011001";
	        else 
	            R <= R - 1;
	        end if;
	    end if;
		
		if btn_right = '1' then
			R <= "01011001";
		end if;
	    if R = "00000000" then
	        R <= "01011001";
		end if;
	end process;	

    led <= R;
end x;
