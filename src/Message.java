import java.io.Serializable;

public class Message implements Serializable{
	private static final long serialVersionUID = 984480579929266084L;
	MessageType type;
	String msg;
	
	public Message(MessageType type, String msg) {
		this.type = type;
		this.msg = msg;
	}
	
	public String getText() {
		return msg;
	}
	
	public MessageType getMsgType() {
		return type;
	}
	
	public void setText(String msg) {
		this.msg =  msg;
	}
	
	public void setMsgType(MessageType type) {
		this.type = type;
	}
}