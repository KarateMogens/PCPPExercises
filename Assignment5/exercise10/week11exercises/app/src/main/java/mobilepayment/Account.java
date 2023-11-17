package mobilepayment;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

public class Account extends AbstractBehavior<Account.AccountCommand> {

  /* --- Messages ------------------------------------- */
  public interface AccountCommand {
  }
  // Feel free to add message types at your convenience
  public static final class UpdateBalance implements AccountCommand {

    private final double value;

    public UpdateBalance(double value) {
      this.value = value;
    }

    public double getValue() {
      return value;
    }
    
  }

  public static final class PrintBalance implements AccountCommand {

  }

  /* --- State ---------------------------------------- */
  // To be Implemented
  private double balance;

  /* --- Constructor ---------------------------------- */
  // Feel free to extend the contructor at your convenience
  private Account(ActorContext<AccountCommand> context) {
    super(context);
  }

  /* --- Actor initial state -------------------------- */
  // To be Implemented
  public static Behavior<AccountCommand> create() {
    return Behaviors.setup(context -> new Account(context));
  }

  /* --- Message handling ----------------------------- */
  @Override
  public Receive<AccountCommand> createReceive() {
    return newReceiveBuilder()
        .onMessage(UpdateBalance.class, value -> this.onUpdateBalance(value))
        .onMessage(PrintBalance.class, this::onPrintBalance)
        .build();
  }

  /* --- Handlers ------------------------------------- */
  // To be Implemented
  private Behavior<AccountCommand> onUpdateBalance(UpdateBalance message) {
    this.balance += message.getValue();
    getContext().getLog().info("Update balance to value " + this.balance + " on account: " + getContext().getSelf().path().name(), getContext().getSelf().path().name());
    return this;
  }

   private Behavior<AccountCommand> onPrintBalance(PrintBalance message) {
    getContext().getLog().info("Current balance: " + this.balance + " on account: " + getContext().getSelf().path().name(), getContext().getSelf().path().name());
    return this;
  }
}
