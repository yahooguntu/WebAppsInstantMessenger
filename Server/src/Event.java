
public class Event
{
	/*
	 * Codes are:
	 * 0  - CREATE ACCOUNT		C->S
	 * 1  - LOGON				C->S
	 * 2  - LOGOFF				C->S
	 * 3  - MESSAGE				C<->S
	 * 4  - BUDDY ON			S->C
	 * 5  - BUDDY OFF			S->C
	 * 6  - SUCCESSFUL LOGON	S->C
	 * 7  - FAILED LOGON		S->C
	 * 8  - ADD BUDDY			C->S
	 * 9  - REMOVE BUDDY		C->S
	 * 10 - TYPING				C<->S
	 * 11 - ENTERED TEXT		C<->S
	 * 12 - MESSAGE FAILED		S->C
	 * 13 - SET PROFILE			C->S
	 * 14 - GET PROFILE 		S->C
	 */
	
	public int eventCode;
	public String msg1 = null;
	public String msg2 = null;
	
	Event(int eventCode, String msg1, String msg2)
	{
		this.msg1 = msg1;
		this.msg2 = msg2;
	}
	
	Event(int eventCode, String msg)
	{
		this.msg1 = msg;
	}
	
	public String toString()
	{
		return "[code=" + eventCode + ",msg1=" + msg1 + ",msg2=" + msg2 + "]";
	}
}
