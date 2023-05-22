package ap.ex2.bookscrabble.common;

public class Protocol {
    // messages from guest to host
    public static final char GUEST_LOGIN_REQUEST = '0';

    // messages from host to guest
    public static final char HOST_LOGIN_ACCEPT = '1';
    public static final char HOST_LOGIN_REJECT_FULL = '2';
    public static final char HOST_LOGIN_REJECT_NICKNAME = '3';
    public static final char PLAYER_UPLOAD = '4';
    public static final char START_GAME = '5';
    public static final char END_GAME = '6';

//cursors parking:| | | |

     static final char YOUR_TURN = '7';
    public static final char NOT_YOUR_TURN = '8';
    public static final char BOARD_ASSIGNMENT_ACCEPTED = '9';
    public static final char BOARD_ASSIGNMENT_REJECTED_CHALLENGE = 'A';
    public static final char BOARD_ASSIGNMENT_REJECTED_QUERY = 'B';
    public static final char GOT_NEW_TILE = 'C';
    public static final char BOARD_UPDATED_BY_ANOTHER_PLAYER = 'D';

}
