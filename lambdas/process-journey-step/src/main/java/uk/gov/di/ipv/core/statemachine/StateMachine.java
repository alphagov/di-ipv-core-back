package uk.gov.di.ipv.core.statemachine;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StateMachine {

    private State initialState;
    private Map<String, State> states = new HashMap<>();

    public StateMachine(StateMachineInitializer initializer) throws IOException {
        this.states = initializer.initialize();
    }

    public StateMachine withState(State state) {
        states.put(state.getName(), state);
        return this;
    }

    public StateMachineResult transition(String startState, String event, Context context)
            throws UnknownEventException, UnknownStateException {
        var state = states.get(startState);

        if (state == null) {
            throw new UnknownStateException(String.format("Invalid state: %s", startState));
        }
        return state.transition(event, context);
    }
}
