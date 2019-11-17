package railway_system.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class Ticket {
    private int ticketId;
    private int trainId;
    private int stationIdFrom;
    private int stationIdTo;
    private int individualId;
    private String firstName;
    private String secondName;
    private String documentType;
    private String documentId;
    private boolean waitingRefund;

}
