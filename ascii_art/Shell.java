package ascii_art;

// Written by Avinoam Nukrai, Winter 2021 Hebrew U - OOP course


import ascii_art.img_to_char.BrightnessImgCharMatcher;
import ascii_output.AsciiOutput;
import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
import image.Image;
import java.util.*;


/**
 * This class is responsible for the supporter of the functionality of getting and
 * executing user commands - Control the ascii table and the output ascii image output source and resolution.
 */
public class Shell {

    // Constants
    // ~~~~~~~~ Commands ~~~~~~~~~~~~~
    private static final String CMD_ADD = "add";
    private static final String CMD_GET_CHARS = "chars";
    private static final String CMD_EXIT = "exit";
    private static final String CMD_REMOVE = "remove";
    private static final String CMD_RES = "res";
    private static final String CMD_CONSOLE = "console";
    private static final String CMD_RENDER = "render";
    private static final String ALL_COMMAND = "all";
    private static final String SPACE_COMMAND = "space";
    private static final String DOWN = "down";
    private static final String UP = "up";
    private static final String CONSOLE = "console";
    private static final String EMPTY_STRING = "";
    // ~~~~~~~~~ Errors ~~~~~~~~~~~~~~
    private static final String ARROWS = ">>> ";
    private static final String INVALID_ARGS_NUM_ERROR = "Invalid number of arguments! Please enter only 2 arguments.";
    private static final String INVALID_COMMAND_ERROR = "Please enter one of the following commands: add/remove/res/chars";
    private static final String ADD_VARS_ERROR = "Please enter  your command  as follow: add [all/space/a-z/(a-z)]";
    private static final String REMOVE_VARS_ERROR = "Please enter your command as: remove [all/space/a-z/(a-z)]";
    private static final String CHARS_COMMAND_ERROR = "Please Enter a valid command, as follow: 'chars'";
    private static final String RES_CHAR_ERROR = "Please enter your command as following: res (up/remove)";
    // ~~~~~~~~ Messages ~~~~~~~~~~~~~
    private static final String RES_UPDATE_MSG = "Width set to ";
    private static final String MAX_RESOLUTION_MSG = "You've reach to the maximal pixels possible";
    private static final String MIN_RESOLUTION_MSG = "You've reach to the  minimal pixels possible";
    // ~~~~~~~~ Nums ~~~~~~~~~~~~~~~~~
    private static final int CMD_INPUT_LENGTH = 2;
    private static final int ADD_THREE_COMMAND = 3;
    private static final int MIN_PIXELS_PER_CHAR = 2;
    private static final int INITIAL_CHARS_IN_ROW = 64;
    private static final int FACTOR = 2;
    // ~~~~~~~~ Others ~~~~~~~~~~~~~~
    private static final char DASH = '-';
    private static final int MIN_ASCII_CHAR = (int)' ';
    private static final int MAX_ASCII_CHAR = (int)'~';
    private static final char EMPTY_CHAR_VAR = ' ';
    private static final String OUTPUT_FILENAME = "out.html";
    private static final String FONT_NAME = "Courier New";


    // Data members
    private Set<Character> charSet = new HashSet<>(); // The data structure of all the possible ascii chars
    private final Image img; // The given image to convert
    private String render; // The output source (html, console)
    private final AsciiOutput htmlOutput;
    private final AsciiOutput consoleOutput;
    private AsciiOutput asciiOutput;
    private final BrightnessImgCharMatcher charMatcher;
    private final int minCharsInRow;
    private final int maxCharsInRow;
    private int charsInRow;

    // Ctor
    public Shell(Image img) {
        this.img = img;
        this.render = OUTPUT_FILENAME; // as default is html file
        this.htmlOutput = new HtmlAsciiOutput(OUTPUT_FILENAME, FONT_NAME);
        this.consoleOutput = new ConsoleAsciiOutput();
        this.asciiOutput = htmlOutput;
        this.charMatcher = new BrightnessImgCharMatcher(img, FONT_NAME);
        this.minCharsInRow = Math.max(1, img.getWidth()/img.getHeight());
        this.maxCharsInRow = img.getWidth() / MIN_PIXELS_PER_CHAR;
        this.charsInRow = Math.max(Math.min(INITIAL_CHARS_IN_ROW, maxCharsInRow), minCharsInRow);
        try {Collections.addAll(this.charSet,'0', '1', '2', '3', '4', '5', '6', '7', '8', '9');} // as default
        catch (Exception ignored){}
    }

    // ~~~~~~~~~~~~~~~~~~ Methods ~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * This method is responsible for execute the command that given by the user, first by getting the
     * input from the user, second by parsing it and third by check validation of it and execute the right command.
     */
    public void run(){
        System.out.print(ARROWS);
        String[] userInput = getAndParseUserInput(); // getting input from the user
        String userCommand = parseUserCommand(userInput); // parse the input
        String userVars = parseUserVars(userInput); // parse the variables that given by the user
        boolean ifExitCommand = true;
        int commandIndicator; // Will indicate if add or remove command has chosen
        while(true) {
            try{
                if (userInput.length > CMD_INPUT_LENGTH){
                    throwException(INVALID_ARGS_NUM_ERROR);
                }
                switch (userCommand){
                    case CMD_EXIT:
                        ifExitCommand = false;
                        break;
                    case CMD_GET_CHARS:
                        this.showChars(userVars);
                        break;
                    case CMD_ADD:
                        commandIndicator = 1;
                        this.addAndRemoveCommands(userVars, commandIndicator);
                        break;
                    case CMD_REMOVE:
                        commandIndicator = 0;
                        this.addAndRemoveCommands(userVars, commandIndicator);
                        break;
                    case CMD_RES:
                        this.resolutionCommand(userVars);
                        break;
                    case CMD_CONSOLE:
                        this.changeRenderCommand();
                        break;
                    case CMD_RENDER:
                        this.renderImage();
                        break;
                    default:
                        throwException(INVALID_COMMAND_ERROR);
                }
            }

            catch (Exception exception){ System.out.println(exception.toString()); } // invalid command has given
            if (!ifExitCommand) break;
            System.out.print(ARROWS);
            userInput = getAndParseUserInput();
            userCommand = parseUserCommand(userInput);
            userVars = parseUserVars(userInput);
        }
    }

    /**
     * This method is printing to the screen all the possible ascii chars in the program
     * @param userVarsInput - The variables that has given by the user.
     * @throws Exception - invalid 'chars' command has given.
     */
    private void showChars(String userVarsInput) throws Exception{
        if (userVarsInput.equals(EMPTY_STRING)){
            charSet.stream().sorted().forEach(c -> System.out.print(c + " "));
            System.out.println();
            return;
        }
        throwException(CHARS_COMMAND_ERROR); // Invalid command has given
    }

    /**
     * This method is responsible for execute the 'render' command.
     * First check it charSet is not empty then creates the final ascii char array and deliver it to the right
     * output source file, by the value this.render (String).
     */
    private void renderImage(){
         if (this.charSet.size() > 0) {
             Character[] newCharSet = this.charSet.toArray(Character[]::new); // new array of charSet items
             char[][] charSet = charMatcher.chooseChars(this.charsInRow, newCharSet);
             if (this.render.equals(CMD_CONSOLE)) this.asciiOutput = this.consoleOutput;
             else this.asciiOutput = this.htmlOutput;
             this.asciiOutput.output(charSet);
         }
    }


    /**
     * This method his responsible for executing the command of change the output file
     * source (this.render), from 'html' to the 'console'.
     */
    private void changeRenderCommand() { this.render = CONSOLE; }


    /**
     * This method is responsible for the resolution command in the program
     * @param userInputVars - The vars of the user after the base command
     * @throws Exception - throwing an exception if invalid command where given
     */
    private void resolutionCommand(String userInputVars) throws Exception {
        if (userInputVars.equals(UP)) resUpCommand();
        else if (userInputVars.equals(DOWN)) resDownCommand();
        else throwException(RES_CHAR_ERROR);
    }

    /**
     * This method is responsible for increasing the resolution of the img, by the formula that has given.
     */
    private void resUpCommand(){
        if (charsInRow * FACTOR <= maxCharsInRow){
            this.charsInRow *= FACTOR;
            System.out.println(RES_UPDATE_MSG + charsInRow);
        }
        else System.out.println(MAX_RESOLUTION_MSG);
    }

    /**
     * This method is responsible for decreasing the resolution of the img, by the formula that has given.
     */
    private void resDownCommand(){
        if (charsInRow / FACTOR >= minCharsInRow){
            this.charsInRow /= FACTOR;
            System.out.println(RES_UPDATE_MSG + charsInRow);
        }
        else System.out.println(MIN_RESOLUTION_MSG);
    }


    /**
     * This method is responsible for the add\remove commands in all there cases
     * @param userInputVars - the user variables input (after the basic commands)
     *        commandIndicator - a binary number, means 0 or 1 that indicates if we want to add or remove command
     * @throws Exception - throws exception in case of invalid variable inserted
     */
    private void addAndRemoveCommands(String userInputVars, int commandIndicator) throws Exception {
        // add only one char to the charSet
        if (userInputVars.length() == 1) {
            if (commandIndicator == 1) this.charSet.add(userInputVars.charAt(0));
            else this.charSet.remove((userInputVars.charAt(0)));
            return;
        }
        // space command
        if (userInputVars.equals(SPACE_COMMAND)) {
            if (commandIndicator == 1) this.charSet.add(EMPTY_CHAR_VAR);
            else this.charSet.remove(EMPTY_CHAR_VAR);
            return;
        }
        // all command
        if (userInputVars.equals(ALL_COMMAND)) {
            // add all the ascii chars to the charSet
            for (int ch = MIN_ASCII_CHAR; ch <= MAX_ASCII_CHAR; ch++){
                if (commandIndicator == 1) this.charSet.add((char) ch);
                else this.charSet.remove((char) ch);
            }
            return;
        }
        // range command
        if (userInputVars.length() == ADD_THREE_COMMAND) {
            if (userInputVars.charAt(1) == DASH) {
                // add all the chars between the correct range to the charSet
                int lowerChar = userInputVars.charAt(0);
                int higherChar = userInputVars.charAt(2);
                if (lowerChar > higherChar){
                    int tempChar = higherChar;
                    higherChar = lowerChar;
                    lowerChar = tempChar;
                }
                for (int ch = lowerChar; ch <= higherChar; ch++){
                    if (commandIndicator == 1) this.charSet.add((char) ch);
                    else this.charSet.remove((char) ch);
                }
            }
        }
        // invalid command has given, therefore throws exception
        else if (commandIndicator == 1) throwException(ADD_VARS_ERROR);
        else throwException(REMOVE_VARS_ERROR);
    }


    /**
     * This method is responsible for getting the user input and parse it
     * @return String array that represent the user input after parsing it to sub words (strings)
     */
    private String[] getAndParseUserInput(){
        Scanner scanner = new Scanner(System.in);
        String cmdInput = scanner.nextLine().trim(); // trim() in java = strip() in python
        return cmdInput.split("\\s+"); // split the user input into sub strings
    }

    /**
     * This method is responsible for parse ones again the user input - removing white spaces and split the vars
     * @param userWords - all user input after splitting into sub strings
     * @return String - represent all the vars of the user (first build as StringBuilder for mutable type)
     */
    private String parseUserVars(String[] userWords) {
        StringBuilder allParams = new StringBuilder();
        for (int i = 1; i < userWords.length; i++) {
            allParams.append(userWords[i]);
        }
        return allParams.toString(); // returns as a string
    }

    /**
     * This method is determines the command of the user
     * @param userWords - Strings array that contains the user input after parsing
     * @return String - The command of the user
     */
    private String parseUserCommand(String[] userWords){
        String commandToReturn = EMPTY_STRING;
        if (!userWords[0].equals(EMPTY_STRING)){
            commandToReturn = userWords[0];
        }
        return commandToReturn.toLowerCase();
    }

    /**
     * This method is responsible for throwing a simple exception
     * @param msgToThrow - msg to write in the exception
     * @throws Exception - throwing exception with the given msg
     */
    private void throwException(String msgToThrow) throws Exception{
        throw new Exception(msgToThrow);
    }

}
