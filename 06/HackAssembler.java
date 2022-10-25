import java.io.File;
// import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class HackAssembler
{
	public static void main(String[] args)
	{
		// Initialize
		// Opens the input file (Prog.asm) and gets ready to process it
		String filename;
		if (args.length == 0)
		{
			Scanner sc = new Scanner(System.in);
			System.out.print("Enter filename (+ extension): ");
			filename = sc.nextLine();
			sc.close();
		}
		else
		{
			filename = args[0];
		}

		File ogFile = new File(filename);
		Scanner scFile, hFile;
		try
		{
			scFile = new Scanner(ogFile);
			System.out.println("Translating " + filename + " into hack code.\nContents of file:\n");

			// create output file
			String outFilename = filename.substring(0, filename.lastIndexOf(".")) + ".hack";
			File hackFile = new File(outFilename);

			SymbolTable sTable = new SymbolTable();
			// do first pass
			firstPass(ogFile, sTable);

			// do second pass
			secondPass(ogFile, hackFile, sTable);

			hFile = new Scanner(new File(outFilename));
			while (hFile.hasNextLine())
				System.out.println(hFile.nextLine());

			// close files
			scFile.close();

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	/**
	 * Reads the program lines, one by one focusing only on (label) declarations.
	 * Adds the found labels to the symbol table
	 * 
	 * @throws IOException
	 */
	public static void firstPass(File inFile, SymbolTable sTable) throws IOException
	{
		Parser p = new Parser(inFile);
		int addr = 0;

		while (p.hasMoreCommands())
		{
			p.advance();

			Parser.CommandType instructionType = p.instructionType();

			if (instructionType == null)
				throw new IllegalStateException("Syntax error at instruction " + (addr + 1));

			switch (instructionType)
			{
				case A_COMMAND:
				case C_COMMAND:
					addr++;
					break;
				case L_COMMAND:
					String symbol = p.symbol();

					if (Character.isDigit(symbol.charAt(0)))
						throw new IllegalStateException("Symbol syntax error at instruction " + (addr + 1));

					// add label to symbol table
					sTable.addEntry(symbol, addr);
			}
		}
	}

	/**
	 * While there are more lines to process: Gets the next instruction, and parses
	 * it If the instruction is @symbol If symbol is not in the symbol table, adds
	 * it Translates the symbol into its binary value If the instruction is dest
	 * =comp ; jump Translates each of the three fields into its binary value
	 * Assembles the binary values into a string of sixteen 0’s and 1’s Writes the
	 * string to the output file.
	 * 
	 * @throws IOException
	 */
	public static void secondPass(File inFile, File outFile, SymbolTable sTable) throws IOException
	{
		Parser p = new Parser(inFile);
		int currentAddr = 16;

		FileWriter fileWrite = null;
		try
		{
			fileWrite = new FileWriter(outFile);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		// fileWrite.write("TEST FILE WRITER\nTHIS IS A NEWLINE");

		String resultOfLine;

		while (p.hasMoreCommands())
		{
			p.advance();

			switch (p.instructionType())
			{
				case A_COMMAND:
					String symbol = p.symbol();
					boolean isDecimal = Character.isDigit(symbol.charAt(0));

					// add symbol to table if it is not a symbol
					if (!isDecimal && !sTable.contains(symbol))
						sTable.addEntry(symbol, currentAddr++);

					// get the integer value of the symbol
					int value = isDecimal ? Integer.parseInt(symbol) : sTable.getAddress(symbol);

					resultOfLine = "0" + String.format("%15s", Integer.toBinaryString(value)).replaceAll(" ", "0") + "\n";

					try
					{
						fileWrite.append(resultOfLine);
					}
					catch (IOException e)
					{
						throw new IllegalStateException(e.getMessage());
					}

					break;
				case C_COMMAND:
					// get binary of comp, dest, and jump
					String comp = Code.comp(p.comp());
					String dest = Code.dest(p.dest());
					String jump = Code.jump(p.jump());

					resultOfLine = "111" + comp + dest + jump + "\n";

					try
					{
						fileWrite.write(resultOfLine);
					}
					catch (IOException e)
					{
						throw new IllegalStateException(e.getMessage());
					}

					break;
				default:
			}
		}

		try
		{
			fileWrite.close();
		}
		catch (IOException e)
		{
			throw new IllegalStateException(e.getMessage());
		}
	}
}