package mobilepayment;

import java.util.ArrayList;
import java.util.Arrays;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

public class Guardian extends AbstractBehavior<Guardian.GuardianCommand> {

  /* --- Messages ------------------------------------- */
  public interface GuardianCommand {
  }

  // Feel free to add message types at your convenience
  public static final class KickOff implements GuardianCommand {

  }

  /* --- State ---------------------------------------- */
  // empty

  /* --- Constructor ---------------------------------- */
  private Guardian(ActorContext<GuardianCommand> context) {
    super(context);
  }

  /* --- Actor initial state -------------------------- */
  // To be implemented
  public static Behavior<Guardian.GuardianCommand> create() {
    return Behaviors.setup(Guardian::new);
  }

  /* --- Message handling ----------------------------- */
  @Override
  public Receive<GuardianCommand> createReceive() {
    return newReceiveBuilder()
        .onMessage(KickOff.class, this::onKickOff)
        .build();
  }

  /* --- Handlers ------------------------------------- */
  // To be implemented
  private Behavior<GuardianCommand> onKickOff(KickOff msg) {
    //spawn mb1
    ActorRef<MobileApp.MobileAppCommand> mb1 = getContext().spawn(MobileApp.create(), "mb1");

    //spawn b1
    ActorRef<Bank.BankCommand> b1 = getContext().spawn(Bank.create(), "b1");
    
    //spawn b1
    ActorRef<Bank.BankCommand> b2 = getContext().spawn(Bank.create(), "b2");
    
    //spawn a1 & a2
    ActorRef<Account.AccountCommand> a1 = getContext().spawn(Account.create(), "a1");
    
    ActorRef<Account.AccountCommand> a2 = getContext().spawn(Account.create(), "a2");

    b1.tell(new Bank.AddAccount(a1));
    b1.tell(new Bank.AddAccount(a2));
    
    b2.tell(new Bank.AddAccount(a1));
    b2.tell(new Bank.AddAccount(a2));
    
    ArrayList<String> accounts = new ArrayList<>(Arrays.asList( "a1","a2" ));
    
    mb1.tell(new MobileApp.MakeTransaction(b1, "a1", "a2", 100.25));
    mb1.tell(new MobileApp.MakeTransaction(b2, "a2", "a1", 100.25));
    mb1.tell(new MobileApp.MakePayments(b1, accounts));
    

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    a1.tell(new Account.PrintBalance());
    a2.tell(new Account.PrintBalance());
    return this;
  }
}
