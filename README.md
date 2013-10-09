AI-Research (CS 493)
===================

This repository holds our Artificial Intelligence research. This specific project focuses on trying to discover how an agent can use its memories of past actions/results to help it intelligently navigate an unknown environment. Presently, we have tried placing the agent in two different environments which are described below. 

### Passphrase Environment

The passphrase environment places the agent in a situation where it must guess a passphrase of characters that is equivalent to the environment’s password. Extraneous characters may be included in the passphrase the agent guesses. Thus, as long as at some point within a string of characters the agent enters the characters of the in the right order, it will be successful. The environment will inform the agent once it successfully enters an adequate passphrase.

The catch however is that the environment includes a list of equivalencies. An equivalency is a mapping of (at the moment) two characters to a single character.  For example:

    ab -> c
    qr -> d
    hp -> n

The environment will accept the left hand side of the equivalency in place of the right hand side, even though it is not technically a correct character in the passphrase. This means the environment will accept a passphrase from the agent, even if it is not the optimal/correct password. For example, if you had an password `abc`, and an equivalency `a -> df`, then the environment would accept both `abc` and `dfbc` from the agent.

The code important to this environment is contained in `Equiv.java`, `Passphrase.java`, `PassphraseAgent.java`, and `PassphraseEnvironment.java`. 
