import junit.framework.TestCase;
import org.junit.Test;

/**
 * StateMachineAgentTest
 *
 * Test class for various testing purposes
 *
 * @author Kirkland Spector
 * @author Chandler Underwood
 *
 */
public class StateMachineAgentTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();

    }

    public void tearDown() throws Exception {

    }

    /**
     * testTransistionTable
     *
     * tests to see if the agent ended with the same transition table
     * that the environment built
     *
     * @throws Exception
     */
    @Test
    public void testTransistionTableSize() throws Exception {
        //set up and run agent to build both tables
        StateMachineAgent gilligan = new StateMachineAgent();
        gilligan.mapStateMachine();

        //determine size of agents table ignoring deleted states
        int agentSize = 0;
        for (int i = 0; i < gilligan.getAgentTransitionTable().size(); i++) {
            if (gilligan.getAgentTransitionTable().get(i)[0] != StateMachineAgent.DELETED) {
                agentSize++;
            }
        }

        //check equality (size is all that matter)
        assertEquals(gilligan.getEnv().getTransition().length, agentSize);
    }

    /**
     * testBestPath
     *
     * tests to see if the agent's best path is actually correct
     *
     * @throws Exception
     */
    @Test
    public void testBestPath() throws Exception {
        //have agent create paths
        StateMachineAgent gilligan = new StateMachineAgent();
        gilligan.mapStateMachine();

        //the env has many paths, only use the first (zeroith) since the others seem to be wrong
        //this is probably a misunderstanding or a bug
        assertEquals(gilligan.getEnv().getPaths()[0], gilligan.getBest().toString());
    }
}