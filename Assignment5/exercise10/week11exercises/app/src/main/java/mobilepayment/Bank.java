package mobilepayment;

import java.util.HashMap;
import java.util.HashSet;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;


import mobilepayment.Account.AccountCommand;
import mobilepayment.Account.UpdateBalance;
import mobilepayment.Account;



public class Bank extends AbstractBehavior<Bank.BankCommand> {

    /* --- Messages ------------------------------------- */
    public interface BankCommand {
    }
    // Feel free to add message types at your convenience

    public static final class Transaction implements BankCommand {

        private final String to;
        private final String from;
        private final double value;

        public Transaction(String from, String to, double value) {
            this.to = to;
            this.from = from;
            this.value = value;
        }

        public double getValue() {
            return value;
        }

        public String getTo() {
            return to;
        }

        public String getFrom() {
            return from;
        }
    }

    public static final class AddAccount implements BankCommand {

        private final ActorRef<Account.AccountCommand> account;
        
        public AddAccount(ActorRef<Account.AccountCommand> account) {
            this.account = account;
        }

        public ActorRef<Account.AccountCommand> getAccount() {
            return this.account;
        }

    }

    /* --- State ---------------------------------------- */
    // To be Implemented
    private HashMap<String, ActorRef<Account.AccountCommand>> accountActors;

    /* --- Constructor ---------------------------------- */
    // Feel free to extend the contructor at your convenience
    private Bank(ActorContext<BankCommand> context) {
        super(context);
        this.accountActors = new HashMap<>();
    }

    /* --- Actor initial state -------------------------- */
    // To be Implemented
    public static Behavior<BankCommand> create() {
        return Behaviors.setup(context -> new Bank(context));
    }

    /* --- Message handling ----------------------------- */
    @Override
    public Receive<BankCommand> createReceive() {
        return newReceiveBuilder()
                // To be implemented
                .onMessage(AddAccount.class, this::onAddAccount)
                .onMessage(Transaction.class, this::onTransaction)
                .build();
    }

    /* --- Handlers ------------------------------------- */
    // To be Implemented
    private Behavior<BankCommand> onAddAccount(AddAccount message) {
        ActorRef<Account.AccountCommand> actorRef = message.getAccount();
        this.accountActors.put(actorRef.path().name(), actorRef);
        getContext().getLog().info("Added account: " + actorRef.path().name() + " to bank: " + getContext().getSelf().path().name(), getContext().getSelf().path().name());
        return this;
    }

    private Behavior<BankCommand> onTransaction(Transaction message) {
        ActorRef<Account.AccountCommand> from = accountActors.get(message.getFrom());
        ActorRef<Account.AccountCommand> to = accountActors.get(message.getTo());
        
        double value = message.getValue();
        from.tell(new Account.UpdateBalance(-value));
        to.tell(new Account.UpdateBalance(value));
        
        return this;
    }
  
}
