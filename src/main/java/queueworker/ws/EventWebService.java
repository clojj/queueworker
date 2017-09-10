package queueworker.ws;

import queueworker.ejb.Worker;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("event")
public class EventWebService {
    
    @Inject
    private Worker worker;
    
    @POST
    @Path("ping")
    public String ping() {
        return String.valueOf(worker.addEventDebounced());
    }
    
}
