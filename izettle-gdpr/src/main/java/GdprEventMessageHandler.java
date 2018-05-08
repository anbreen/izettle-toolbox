import com.izettle.messaging.handler.MessageHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import model.GdprEvent;
import model.GdprEventHandler;

@Data
@Slf4j
public class GdprEventMessageHandler implements MessageHandler<GdprEvent> {

    private GdprEventHandler handler;

    @Override
    public void handle(GdprEvent gdprEvent) throws Exception {

        switch (gdprEvent.getType()) {
            case FORGET:
                handler.handleForget(gdprEvent);
                break;
            case PORT_DATA:
                handler.handleExport(gdprEvent);
                break;
            default:
                break;
        }

    }
}
