## Exercise 12

1. The Server should only send tasks to workers that are idle. If a task needs to be processed and there are no idle workers, then it should be placed in a list of pending tasks. Define the state of the Server so that it can keep track of idle workers, busy workers and pending tasks. Explain why the elements of the state that you added are sufficient to keep track of busy workers and pending tasks.

**Answer**:
>
> To keep track of idle workers, busy workers and pending tasks we added the three fields: 
>   	
	private final Queue<ActorRef<Worker.WorkerCommand>> idleWorkers  	
	private final Queue<ActorRef<Worker.WorkerCommand>> busyWorkers  		
	private final Queue<Worker.ComputeTask> pendingTasks  	
>  	
> For the idle and busy workers we implemented two queues, both consisting of ActorRef to Workers. We chose to implement it using a queue such that the first worker that comes in, is also the first to be chosen. The data structure chosen works well for keeping track of the workers, because it is easy to enqueue and dequeue workers from the queues.  
For the pending tasks we also made a queue, but of type Worker.ComputeTask. ComputeTask is a message in the worker class, and is what we have to send to the idle workers.

2. As mentioned above, the Server should always have a minimum number of active workers, and a limit in the number of workers it can hold active at the same time. Extend the state of the Server with two variables containing the number of minimum minWorkers and maximum workers maxWorkers. Also, write the constructor of the Server so that it spawns minWorkers workers. The constructor must properly initialize all the elements of the state, i.e., idle and busy workers, pending tasks, and min/max workers. Explain the implementation of the constructor and why the new elements of the state are sufficient for this exercise.

**Answer:**
>
> We added two fields to the state:
> 
	private int minWorkers;  	
	private int maxWorkers;  
>
> The constructor is implemented like this:
> 
	private Server(ActorContext<ServerCommand> context, int minWorkers, int maxWorkers) {
		super(context);
		this.minWorkers = minWorkers;
		this.maxWorkers = maxWorkers;
		idleWorkers = new LinkedList<>();
		busyWorkers = new LinkedList<>();
		pendingTasks = new LinkedList<>();

		for (int i = 0; i < minWorkers; i++) {
			final ActorRef<Worker.WorkerCommand> newWorker = getContext().spawn(Worker.create(getContext().getSelf())"Worker" + i);
			getContext().watch(newWorker);
			idleWorkers.add(newWorker);
		}

	}
	
> 
> In the constructor we take in the number of min and max workers as parameters, and save it in the variables in the state. This also means that the number of min and max workers are initialized when the server is spawned. 
> It is sufficient to have the number of min and max workers as integers, because this way we can always guarantee to have a specific number of workers ready, however we might add more workers if the workload is high. We have not implemented where a worker is removed if the workload is low, but if we did so we could make sure to have the dynamic load balancing mechanism by having a span between the two integers. 
> Furthermore we instantiated the three queues as linkedlists.  
> Finally we looped from 0 up to the number of minWorkers where we for each iteration spawned a new worker and added it to our queue of idle workers. 

3. Now we move to handling a list of tasks sent from a client. Write the message handler for messages of type ComputeTasks, which contains the list of tasks sent by the client. For each task in the list, the Server
must proceed as follows:  
	(a) If there are idle workers the task is sent to a worker—using a ComputeTask message.  
	(b) If there are no idle workers, but the number of busy workers is less than maxWorkers, then spawn a new worker and send the task.  
	(c) If none of the above conditions hold, then the task must be placed in the list of pending tasks.  
Explain how your implementation addresses these cases.

**Answer:**
> 
> We implemented the following handler:
> 
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

> In the handler for messages of type ComputeTasks, we loop over all tasks. We then have some if-statements checking the three cases described in the assignment.   	
> - The first if statement check if the queue with idle workers are bigger than 0, and if so, we know that we have 'free' idle workers, and we take one from the queue and tell it to compute the task at index i and lastely add this worker to the queue on busy workers.   	
> - The second if statement checks if the queue of idle workers are empty, but we haven't reached the maximum amount of workers. If this is the case we create a new worker and tell it to compute the task at index i and add it to the queue of busy workers.  	
> - The else statement is when none of the other condition holds and if so we know that we have no idle workers, but already have the maximum number of workers, and therefore we add the task at index i to the queue of pending tasks. 

4. If you run the system now, you will notice that two actors will crash due to a division by zero exception.Your task now is to make the server fault-tolerant. The server must watch all the workers it spawns, and, in case any of them fails, a new worker should be spawned and added to the list of idle workers. Explain how your implementation handles this situation. Hint: It might be useful to revisit getContext().watch(...), the class ChildFailed, and onSignal(...).

**Answer:**
>
> We have two places where we spawn new workers - in the constructor and in the handler for messages of type ComputeTasks if the idle workers queue is empty, but we haven't reached the maximum number of workers. In both places we decided to add `getContext().watch(worker)` on the new worker that we spawned. We did so to supervise the children (the workers), such that if one of them crashes due to a failure (for exampe the division by zero exception) we would get a signal.  
> In our message handling, we then added `.onSignal(ChildFailed.class, this::onChildFailed)` to handle if one of the children crashed. The handler looks like this: 

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
> So if one of the children crashes, we get the reference of the crashed child. If it crashed due to an Arithmetic Exception we print out that something went wrong. We then spawn a new worker, that we also watch and add to the queue of idle workers. Because these workers crash due to a task that is not solvable (dividing by 0), there is no purpose of getting the task that the worker was working on and give it to a new worker, hence we just create a new worker and add it to the queue. 

5. As you probably have already noticed, workers send a WorkDone message to the Server when they finish the computation. Here you have to implement the handler for messages of type WorkDone. In particular, when the Server receives this type of message, it must proceed as follows:  
(a) If there are pending tasks, the Server takes one and sends it to the worker.  
(b) If there are no pending tasks, the Server updates the status of the worker (i.e., move it from busy to
idle).  
Explain how your implementation handles the WorkDone message and addresses these cases.


**Answer:**
> To handle the message from a worker when a task is done we implemented the following handler:

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
> Because the message that we recieved contains the reference to the worker that send the message we start by storing it as a local reference. 
> - We then check if the pending tasks queue is not empty, and if this is the case we take a task out of the queue. We then tell the worker to compute the task that we just got out of the queue. 
> - If there are no pending tasks we remove the worker from the busy worker queue and adds it to the idle workers queue.  
> We added this to our handler to our message handler like this `.onMessage(WorkDone.class, this::onWorkDone)`  
