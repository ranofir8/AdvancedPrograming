package ap.ex2.bookscrabble.common;

public class Protocol {
    // messages from guest to host
    public static final char GUEST_LOGIN_REQUEST = '0';
    public static final char BOARD_ASSIGNMENT_REQUEST = 'E';    // QUERY
    public static final char BOARD_CHALLENGE_REQUEST = 'K';     // CHALLENGE
    public static final char SKIP_TURN_REQUEST = 'L';           // SKIP TURN
    public static final char END_GAME_TILE_SUM_RESPONSE = 'N';


    // messages from host to guest
    public static final char GAME_CRASH_ERROR = '~';      // when someone leaves in the middle of the game
    public static final char INVALID_ACTION = '!';

    public static final char HOST_LOGIN_ACCEPT = '1';
    public static final char HOST_LOGIN_REJECT_FULL = '2';
    public static final char HOST_LOGIN_REJECT_NICKNAME = '3';
    public static final char PLAYER_UPLOAD = '4';
    public static final char START_GAME = '5';
    public static final char END_GAME_TILE_SUM_REQUEST = '6';
    public static final char END_GAME_WINNER = '7';

    public static final char TURN_OF = '8';
    public static final char BOARD_ASSIGNMENT_ACCEPTED = '9';
    public static final char BOARD_ASSIGNMENT_REJECTED_CHALLENGE = 'A';
    public static final char BOARD_ASSIGNMENT_ACCEPTED_CHALLENGE = 'B';
    public static final char BOARD_UPDATED_BY_ANOTHER_PLAYER = 'C';
    public static final char UPDATED_PLAYER_SCORE = 'M';
    public static final char SEND_NEW_TILES = 'D';
    public static final char SEND_BOARD = 'O';


    public static final char ERROR_OUTSIDE_BOARD_LIMITS = 'F';              //-1
    public static final char ERROR_NOT_ON_STAR = 'G';                       //-2
    public static final char ERROR_NOT_LEANS_ON_EXISTING_TILES = 'H';       //-3
    public static final char ERROR_NOT_MATCH_BOARD = 'I';                   //-4
    public static final char ERROR_WORD_NOT_LEGAL = 'J';                    //-5

}
