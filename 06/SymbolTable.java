import java.util.HashMap;
import java.util.Map;

public class SymbolTable
{
	private Map<String, Integer> symbols;

	/** Creates and initializes a SymbolTable */
	public SymbolTable()
	{
		symbols = new HashMap<>();
	}

	/** Adds <symbol, address> to the table (void) */
	public void addEntry(String symbol, int address)
	{
		if (!RESERVED_SYM.containsKey(symbol))
			symbols.put(symbol, address);
	}

	/** Checks if symbol exists in the table (boolean) */
	public Boolean contains(String symbol)
	{
		return RESERVED_SYM.containsKey(symbol) || symbols.containsKey(symbol);
	}

	/** Returns the address (int) associated with symbol */
	public int getAddress(String symbol)
	{
		if (RESERVED_SYM.containsKey(symbol))
			return RESERVED_SYM.get(symbol);

		return symbols.get(symbol);
	}

	private static final Map<String, Integer> RESERVED_SYM = new HashMap<>()
	{
		{
			put("SP", 0);
			put("LCL", 1);
			put("ARG", 2);
			put("THIS", 3);
			put("THAT", 4);
			put("R0", 0);
			put("R1", 1);
			put("R2", 2);
			put("R3", 3);
			put("R4", 4);
			put("R5", 5);
			put("R6", 6);
			put("R7", 7);
			put("R8", 8);
			put("R9", 9);
			put("R10", 10);
			put("R11", 11);
			put("R12", 12);
			put("R13", 13);
			put("R14", 14);
			put("R15", 15);
			put("SCREEN", 16384);
			put("KBD", 24576);
		}
	};

}
