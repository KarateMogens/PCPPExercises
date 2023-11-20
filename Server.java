package mathsserver;

// Hint: The imports below may give you hints for solving the exercise.
//       But feel free to change them.

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.ChildFailed;
import akka.actor.typed.Terminated;
import akka.actor.typed.javadsl.*;
import akka.dispatch.sysmsg.Terminate;

import java.util.Queue;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.stream.IntStream;

import com.typesafe.config.ConfigException.Null;

import mathsserver.Task;
import mathsserver.Task.BinaryOperation;
import mathsserver.Worker.ComputeTask;

public class Server extends AbstractBehavior<Server.ServerCommand> {
    /* --- Messages ------------------------------------- */
    public interface ServerCommand { }
    
    public static final class ComputeTasks implements ServerCommand {
		public final List<Task> tasks;
		public final ActorRef<Client.ClientCommand> client;

		public ComputeTasks(List<Task> tasks,
							ActorRef<Client.ClientCommand> client) {
			this.tasks  = tasks;
			this.client = client;
		}
    }

    public static final class WorkDone implements ServerCommand {
		ActorRef<Worker.WorkerCommand> worker;

		public WorkDone(ActorRef<Worker.WorkerCommand> worker) {
			this.worker = worker;
		}
    }
    
    /* --- State ---------------------------------------- */
	private final Queue<ActorRef<Worker.WorkerCommand>> idleWorkers;
	private final Queue<ActorRef<Worker.WorkerCommand>> busyWorkers;
	private final Queue<Worker.ComputeTask> pendingTasks; 
	int minWorkers;
	int maxWorkers;
       

    /* --- Constructor ---------------------------------- */
    private Server(ActorContext<ServerCommand> context,
				   int minWorkers, int maxWorkers) {
		super(context);	
		this.minWorkers = minWorkers;
		this.maxWorkers = maxWorkers;
		idleWorkers  = new LinkedList<>();
		busyWorkers = new LinkedList<>();
		pendingTasks = new LinkedList<>();
		for (int i = 0; i < minWorkers; i++) {
		final ActorRef<Worker.WorkerCommand> worker = getContext().spawn(Worker.create(getContext().getSelf()), "W" + i);
		getContext().watch(worker);
		idleWorkers.add(worker);
		}
    }


    /* --- Actor initial state -------------------------- */
    public static Behavior<ServerCommand> create(int minWorkers, int maxWorkers) {
    	return Behaviors.setup(context -> new Server(context, minWorkers, maxWorkers));
    }
    

    /* --- Message handling ----------------------------- */
    @Override
    public Receive<ServerCommand> createReceive() {
    	return newReceiveBuilder()
    	    .onMessage(ComputeTasks.class, this::onComputeTasks)
    	    .onMessage(WorkDone.class, this::onWorkDone)
			.onSignal(ChildFailed.class, this::onChildFailed)
			//.onSignal(Terminated.class, this::onTerminated)
    	    .build();
    }


    /* --- Handlers ------------------------------------- */
    public Behavior<ServerCommand> onComputeTasks(ComputeTasks msg) {
		for (Task task: msg.tasks) {
			if (idleWorkers.size() > 0 ){
				ActorRef<Worker.WorkerCommand> w = idleWorkers.poll();
				w.tell(new Worker.ComputeTask(task, msg.client));
				busyWorkers.add(w);
			} else if (idleWorkers.isEmpty() && busyWorkers.size() < maxWorkers){
				final ActorRef<Worker.WorkerCommand> worker = getContext().spawn(Worker.create(getContext().getSelf()), "W" + busyWorkers.size()+1);
				worker.tell(new Worker.ComputeTask(task, msg.client));
				getContext().watch(worker);
				busyWorkers.add(worker);
			} else if (idleWorkers.isEmpty()) {
				pendingTasks.add(new Worker.ComputeTask(task, msg.client));
			}
		}
    	return this;
    }

    public Behavior<ServerCommand> onWorkDone(WorkDone msg) {

		if(pendingTasks.size()>0){
			ComputeTask task = pendingTasks.poll();
			new Worker.ComputeTask(task.task, task.client);
		}	else {
			busyWorkers.remove(msg.worker);
			idleWorkers.add(msg.worker);
		}

		return this;	
    }    

	public Behavior<ServerCommand> onChildFailed(ChildFailed msg) {

		ActorRef<Void> crashedChild = msg.getRef();
		// Remove from busyworkers
		busyWorkers.remove(crashedChild);

		if ((msg.getCause() instanceof ArithmeticException)) {
			System.out.println ("An arithmeticException appeared.");
			// System.out.println(pendingTasks.size());
			// System.out.println(idleWorkers.size());
			// System.out.println(busyWorkers.size());
		}

		// New Worker Spawn
		final ActorRef<Worker.WorkerCommand> worker = getContext().spawn(Worker.create(getContext().getSelf()), "x" + busyWorkers.size() + 1);
		getContext().watch(worker);
		idleWorkers.add(worker);

		return this;
	}

	// 	public Behavior<ServerCommand> onTerminated (Terminated msg){
	// 	return this; 
	// }
}
