package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine M = readConfig();

        String settings = _input.nextLine().substring(1);
        setUp(M, settings);
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            _alphabet = new UpperCaseAlphabet();
            String alphabet = _config.nextLine();

            _numRotors = _config.nextInt();
            _pawls = _config.nextInt();

            List<Rotor> allRotors = new ArrayList<Rotor>();

            while(_config.hasNextLine()) { // TODO: Fix this
                allRotors.add(readRotor());
            }

            return new Machine(_alphabet, _numRotors, _pawls, allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        Rotor rotor = null;
        try {
            String name = _config.next();
            String type = _config.next();

            String cycles = _config.nextLine().trim();
            if (type.charAt(0) == 'R') {
                cycles += " " + _config.nextLine().trim();
            }

            Permutation perm = new Permutation(cycles, _alphabet);

            if (type.charAt(0) == 'M') {
                rotor = new MovingRotor(name, perm, type.substring(1));
            }
            else if (type.charAt(0) == 'N') {
                rotor = new FixedRotor(name, perm);
            }
            else if (type.charAt(0) == 'R') {
                rotor = new Reflector(name, perm);
            }
        }
        catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
        return rotor;
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        Scanner scan = new Scanner(settings);
        String[] rotors = new String[_numRotors];

        for (int i = 0; i < _numRotors; i++) {
            rotors[i] = scan.next();
        }

        M.insertRotors(rotors);
        M.setRotors(scan.next());

        String plugboard_pairs = scan.nextLine().trim();

        Permutation perm = new Permutation(plugboard_pairs, _alphabet);
        M.setPlugboard(perm);
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
      if (msg.length() == 0) {
            System.out.println();
      }
      char[] msgChars = msg.toCharArray();
      for (int i = 0; i < msgChars.length; i++) {
        if (i == 5) {
          System.out.print(" ");
          i = 0;
        }
        System.out.print(msgChars[i]);
      }
      System.out.println();
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** The number of rotor slots I have. */
    private int _numRotors;

    /** The number of pawls I have. */
    private int _pawls;
}
