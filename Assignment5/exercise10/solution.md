## Exercise 11.1 

Your task in this exercise is to implement a Mobile Payment system using Akka. The system consists of three types of actors:

• Mobile App: These actors send transactions to the bank corresponding to mobile payments.
• Bank: These actors are responsible for executing the transactions received from the mobile app. That is,
substracting the money from the payer account and adding it to the payee account.
• Account: This actor type models a single bank account. It contains the balance of the account. Also, banks should be able to send positive deposits and negative deposits (withdrawals) in the account.

The directory `code-exercises/week11exercises` contains a source code skeleton for the system that you might find helpful.

![Photo of structure](/Structure.png)

The figure above shows an example of the behavior. In this example, there is a mobile app, `mb1` , a bank, `b1` , and two accounts, `a1` and `a2`. The arrow from `mb1` to `b1` models `mb1` sending a transaction to b1 indicating to transfer 100 DKK from `a1` to `a2` . Likewise, the arrows from `b1` to `a1` and `a2` model the sending of deposits to the corresponding accounts to realise the transaction—the negative deposit can be seen as a withdrawal.

**Mandatory**

1. Design and implement the guardian actor (in `Guardian.java`) and complete the `Main.java` class to start the system. The Main class must send a kick-off message to the guardian. For now, when the guardian receives the kick-off message, it should spawn an MobileApp actor. Finally, explain the design of the guardian, e.g., state (if any), purpose of messages, etc. Also, briefly explain the purpose of the program statements added to the Main.java to start off the actor system.

Note: In this exercise you should only modify the files Main.java and Guardian.java. The code skeleton already contains the minimal actor code to start the MobileApp actor. If your implementation is correct, you will observe a message INFO `mobilepaymentsolution.MobileApp` - Mobile app XXX started! or similar when running the system.

>**Answer:** 
>
>**Guardian Design**
> Within the Guardian class we have defined the internal class: `public static final class KickOff implements GuardianCommand`. This is a simple empty class (i.e. it has no state or non-default constructor). Furthermore we've added the following line to the message handling section:
> 
>        return newReceiveBuilder()
>        .onMessage(KickOff.class, this::onKickOff)
>        .build();
>
> This piece of code describes which internal methods (handler) to call upon recieval of a message. For now we simply respond with a call to the method `onKickOff` upon receive a message of type `KickOff`. As of right now, `onKickoff` simply spawns `mb1`, a `MobileApp` actor.
>
>**Main Class**
>
>       // start actor system
>		final ActorSystem<Guardian.GuardianCommand> guardian = ActorSystem.create(Guardian.create(), "guardian");
>		// init message
>		guardian.tell(new Guardian.KickOff())
> The above piece of code within `Main.java` first instantiates the Guardian actor, by calling its `.create()` method and passing the newly created actor to the `ActorSystem` class. The `ActorSystem` class is a sort of collection of `Actor`, which can be added or looked up through this class.
> After instantiating the guardian, we pass a new message to the Guardian of type `Guardian.KickOff`. 

2. Design and implement the Account actor (see the file Account.java in the skeleton), including the messages it must handle. Explain your design choices, e.g., elements of the state, how it is initialized, purpose of messages, etc.

>**Answer**
>Inside the Account actor we have defined an internal class: `public static final class UpdateBalance implements AccountCommand`. Inside this class there is one private final field called `value`, which is set in the constructor which holds the value that the account should be either incremented or decremented with. The state of the Account actor is the field `private double balance` which holds the Account actor's balance. An Account actor is initialized from the Guardian, via its `create()` method, that uses the constructor of the Account actor. An Account actor can recieve a message of type `UpdateBalance` which is handled in the method `onUpdateBalance` where the Account actor's balance is increased/decreased based on the value transferred. 
>


3. Design and implement the `Bank` actor (see the file `Bank.java` in the skeleton), including the messages it must handle. Explain your design choices, e.g., elements of the state, how it is initialized, purpose of messages, etc.

>**Answer:**
>
> The Actor class `Bank` has a single field (its state), which is:
>
>        private HashMap<String, ActorRef<Account.AccountCommand>> accountActors
>
> This field is a simple look-up between an address (an account name) and its corresponding ActorRef object. We've chosen this format such that a simple string can be used as an account address (similar to an IBAN or account number).
> The Bank actor can receive two kinds of messages: `Transaction` and `AddAccount` which either process a transaction or add an account to its `accountActors` map respectively.
> `Transaction` has three final fields; `String to, String from, double value` as well as getter methods for each value, which is essentially the message payload necessary to carry out a transaction.
> `AddAccount` simply has one field `account`, necessary to add the specified account to its map.
>
> The most interesting handler-method is `onTransaction(Transaction message)`: 
>
>        private Behavior<BankCommand> onTransaction(Transaction message) {
>            ActorRef<Account.AccountCommand> from = accountActors.get(message.getFrom());
>            ActorRef<Account.AccountCommand> to = accountActors.get(message.getTo());
>
>            double value = message.getValue();
>            from.tell(new Account.UpdateBalance(-value));
>            to.tell(new Account.UpdateBalance(value));
>
>            return this;
>        }
>
> In this method, upon recieving a Transaction message, the Bank actor sends out two new `UpdateBalance` messages, with the values to increase and decrease the account balances with. These messages are sent to the sender and receiver `Accounts`.


4. Design and implement the Mobile App actor (see the file `MobileApp.java` in the skeleton), including the messages it must handle. Explain your design choices, e.g., elements of the state, how it is initialized, purpose of messages, etc.

> **Answer:**
>
>The MobileApp actor has an internal class; `public static final class MakeTransaction implements MobileAppCommand` which has four fields, that are all private and final; `ActorRef<Bank.BankCommand> bank`, `String from`, `String to`, `double amount`. The purpose of this Message type is simply to be able to receive a message from the Guardian, telling the `MobileApp` to send a transaction to a Bank actor. The fields of this Message type are based on the fact, that we in the Bank actor decided that the accounts should be represented by Strings, amount by a double, and we are told from the guardian which bank actor should handle the transaction. Again, we implement getter methods for each field.
>
>The MobileApp itself has no state - however, if we were to extend the class to implement more functionality, we might have a list of transaction history or something similar.
> Since we are not given any restrictions on which bank a MobileApp can request to carry out transactions, we also don't have a field of type `ActorRef<Bank.BankCommand>` which could be used to store the bank that a MobileApp is associated with. This could be imagined to be relevant in future implementations.
>
>Like the other actors, a new MobileApp actor is instantiated from the the Guardian, that calls the `create()` method, that calls the constructor of the MobileApp actor. A MobileApp actor recieves a message of type `MakeTransaction` from the guardian, and therefore it has `.onMessage(MakeTransaction.class, this::onMakeTransaction)` in the `createReceive` method (the message handling section). When it recieves a `MakeTransaction` message from the Guardian, it simply uses the payload to send a new `Transaction` message to the specified Bank actor.


5. Update the guardian so that it starts 2 mobile apps, 2 banks, and 2 accounts. The guardian must be able to send a message to mobile app actors to execute a set of payments between 2 accounts in a specified bank. Finally, the guardian must send two payment messages: 1) from a1 to a2 via b1 , and 2) from a2 to a1 via b2 . The amount in these payments is a constant of your choice.

>**Answer:**
>
> See code: `Guardian.java`


6. Modify the mobile app actor so that, when it receives a “make payments” message from the guardian, it sends 100 transactions between the specified accounts and bank with a random amount. 

Hint: You may use Random::ints to generate a stream of random integers. The figure below illustrates the computation.

![hulabalu](/Question6.png) 

>**Answer:**
>
> See code: `MobileApp.java`

7. Update the Account actor so that it can handle a message PrintBalance. The message handler should print the current balance in the target account. Also, update the guardian actor so that it sends PrintBalance messages to accounts 1 and 2 after sending the make payments messages in the previous item.

What balance will be printed? The one before all payments are made, or the one after all payments are made, or anything in between, or anything else? Explain your answer.

>**Answer:**
>
> See code: `Account.java`
>
> The account balance that will be printed is whatever the balance is at the moment in which the Account actor handles the PrintBalance message. That is, the only guarantee that we have, is that the Account actor will execute the contents of the PrintBalance in the order in which it **receives** the messages.

8. Consider a case where two different bank actors send two deposits exactly at the same time to the same account actor. Can data races occur when updating the balance? Explain your answer.

>**Answer:**
>
> There can occur no data races in the above scenario. The account will simply handle the messages sequentially in the order in which it receives them. There is therefore no concurrent read/write to the local state of the Account actor.
