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

        //check equality (size is all that matter)
        assertEquals(gilligan.getEnv().getTransition().length, gilligan.getNonEquivalentStates().size() + 1);//plus one for goal
    }
}