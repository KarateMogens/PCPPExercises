package mathsserver;

// Hint: The imports below may give you hints for solving the exercise.
//       But feel free to change them.

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.ChildFailed;
import akka.actor.typed.Terminated;
import akka.actor.typed.javadsl.*;

import java.util.Queue;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.stream.IntStream;

import mathsserver.Task;
import mathsserver.Task.BinaryOperation;
import mathsserver.Worker.ComputeTask;

public class Server extends AbstractBehavior<Server.ServerCommand> {
	/* --- Messages ------------------------------------- */
	public interface ServerCommand {
	}

	public static final class ComputeTasks implements ServerCommand {
		public final List<Task> tasks;
		public final ActorRef<Client.ClientCommand> client;

		public ComputeTasks(List<Task> tasks,
				ActorRef<Client.ClientCommand> client) {
			this.tasks = tasks;
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
	// To be implemented
	private final Queue<ActorRef<Worker.WorkerCommand>> idleWorkers;
	private final Queue<ActorRef<Worker.WorkerCommand>> busyWorkers;
	private final Queue<Worker.ComputeTask> pendingTasks;
	private int minWorkers;
	private int maxWorkers;

	/* --- Constructor ---------------------------------- */
	private Server(ActorContext<ServerCommand> context, int minWorkers, int maxWorkers) {
		super(context);
		this.minWorkers = minWorkers;
		this.maxWorkers = maxWorkers;
		idleWorkers = new LinkedList<>();
		busyWorkers = new LinkedList<>();
		pendingTasks = new LinkedList<>();

		for (int i = 0; i < minWorkers; i++) {
			final ActorRef<Worker.WorkerCommand> newWorker = getContext().spawn(Worker.create(getContext().getSelf()),
					"Worker" + i);
			getContext().watch(newWorker);
			idleWorkers.add(newWorker);
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
				// .onSignal(Terminated.class, this::onTerminated)
				// To be extended
				.build();
	}

	/* --- Handlers ------------------------------------- */
	public Behavior<ServerCommand> onComputeTasks(ComputeTasks msg) {
		for (int i = 0; i < msg.tasks.size(); i++) {
			if (idleWorkers.size() > 0) {
				final ActorRef<Worker.WorkerCommand> w = idleWorkers.poll();
				w.tell(new Worker.ComputeTask(msg.tasks.get(i), msg.client));
				busyWorkers.add(w);
			} else if (idleWorkers.isEmpty() && busyWorkers.size() < maxWorkers) {
				final ActorRef<Worker.WorkerCommand> w = getContext().spawn(Worker.create(getContext().getSelf()),
						"Worker" + busyWorkers.size() + 1);
				getContext().watch(w);
				w.tell(new Worker.ComputeTask(msg.tasks.get(i), msg.client));
				busyWorkers.add(w);
			} else {
				pendingTasks.add(new Worker.ComputeTask(msg.tasks.get(i), msg.client));
			}
		}
		return this;
	}

	public Behavior<ServerCommand> onWorkDone(WorkDone msg) {
		ActorRef<Worker.WorkerCommand> worker = msg.worker; 

		if (pendingTasks.size() > 0) {
			ComputeTask task = pendingTasks.poll();
			worker.tell(new Worker.ComputeTask(task.task, task.client));
		} else {
			busyWorkers.remove(worker);
			idleWorkers.add(worker);
		}

		return this;
	}


	public Behavior<ServerCommand> onChildFailed(ChildFailed msg) {
		ActorRef<Void> crashedChild = msg.getRef();

		if (msg.getCause() instanceof ArithmeticException) {
			System.out.println("something went wrong - an ArithmeticException happened");
		}

		busyWorkers.remove(crashedChild);

		final ActorRef<Worker.WorkerCommand> w = getContext().spawn(Worker.create(getContext().getSelf()),
				"Worker" + busyWorkers.size() + 10);
		getContext().watch(w);
		idleWorkers.add(w);

		return this;
	}

	// public Behavior<ServerCommand> onTerminated(Terminated msg) {

	// return this;
	// }

}
