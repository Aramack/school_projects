package cmsc433.p3;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
//import java.util.concurrent.PriorityBlockingQueue;
//import java.util.Stack;

/**
 * This file needs to hold your solver to be tested. You can alter the class to
 * extend any class that extends MazeSolver. It must have a constructor that
 * takes in a Maze. It must have a solve() method that returns the datatype
 * List<Direction> which will either be a reference to a list of steps to take
 * or will be null if the maze cannot be solved.
 */
public class StudentMTMazeSolver extends SkippingMazeSolver {
	private getSolution mySol;
	private ExecutorService pool;

	public StudentMTMazeSolver(Maze maze) {
		super(maze);
		mySol = new getSolution();
		// PriorityBlockingQueue<Runnable> pbq = new
		// PriorityBlockingQueue<Runnable>();
		// Runs more consistently than lifo, but takes longer.
		// BlockingQueue<Runnable> fifo = new LinkedBlockingQueue<Runnable>();
		// fifo is also slow
		BlockingQueue<Runnable> lifo = new BlockingLifoQueue<Runnable>();
		pool = new myThreadPoolExecutor(Runtime.getRuntime()
				.availableProcessors(), Runtime.getRuntime()
				.availableProcessors(), 1, TimeUnit.MINUTES, lifo);
	}

	private class myThreadPoolExecutor extends ThreadPoolExecutor {
		private int ThreadsStarted = 0;
		private int ThreadsCompleted = 0;
		private int JobsSubmited = 0;
		private Object masterLock = new Object();

		public myThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
				long keepAliveTime, TimeUnit unit,
				BlockingQueue<Runnable> workQueue) {
			super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
		}

		protected void beforeExecute(Thread t, Runnable r) {
			synchronized (masterLock) {
				ThreadsStarted++;
			}
			super.beforeExecute(t, r);
		}

		protected void afterExecute(Runnable r, Throwable t) {
			synchronized (masterLock) {
				ThreadsCompleted++;
				//System.out.println("started: "+ ThreadsStarted +" Finished: "
				// +ThreadsCompleted + " Submitted: "+ JobsSubmited);
				if (ThreadsStarted == JobsSubmited
						&& ThreadsStarted == ThreadsCompleted) {
					mySol.setSoln(null);
					pool.shutdown();
				}
			}

			super.afterExecute(r, t);
		}

		public void execute(Runnable c) {
			synchronized (masterLock) {
				JobsSubmited++;
			}

			super.execute(c);
		}

		protected void terminated() {
			super.terminated();
		}
	}

	public List<Direction> solve() {
		if (maze.getStart().equals(maze.getEnd())) {
			// Maze starts on the same square it ends on; Dumb corner case
			return new LinkedList<Direction>();
		}
		pool.execute(new Mouse(maze.getStart(), 0, new SolutionTree(null)));
		/*
		 * try { pool.awaitTermination(100, TimeUnit.SECONDS);
		 * 
		 * } catch (InterruptedException e1) { return mySol.getSoln(); }
		 */
		synchronized (mySol.doneNotify) {
			while (!mySol.isDone) {
				try {
					mySol.doneNotify.wait(2000);
				} catch (InterruptedException e) {
					return mySol.getSoln();
				}
			}
		}
		return mySol.getSoln();
	}

	private class Mouse implements Runnable, Comparable<Mouse> {
		private int DistanceCount;
		private Direction last;
		private SolutionTree myTree;
		private Position curr;
		private final Position endLoc = maze.getEnd();

		public Mouse(Position start, int dc, SolutionTree tree) { // starting
			// point and
			curr = start;
			myTree = tree;
			last = myTree.pathFromParent;
			DistanceCount = dc;
		}

		private void doFirst() {
			try {
				byte count = 0;
				boolean canNorth = false;
				boolean canSouth = false;
				boolean canEast = false;
				boolean canWest = false;
				if (maze.canMove(curr, Direction.NORTH)) {
					if (curr.move(Direction.NORTH).equals(endLoc)) {
						solutionFound();
						return;
					}
					count++;
					canNorth = true;
				}
				if (maze.canMove(curr, Direction.SOUTH)) {
					if (curr.move(Direction.SOUTH).equals(endLoc)) {
						solutionFound();
						return;
					}
					count++;
					canSouth = true;
				}
				if (maze.canMove(curr, Direction.EAST)) {
					if (curr.move(Direction.EAST).equals(endLoc)) {
						solutionFound();
						return;
					}
					count++;
					canEast = true;
				}
				if (maze.canMove(curr, Direction.WEST)) {
					if (curr.move(Direction.WEST).equals(endLoc)) {
						solutionFound();
						return;
					}
					count++;
					canWest = true;
				}
				if (count == 0) {
					return;
				}
				maze.getMoves(curr);
				while (count > 1) {
					if (canNorth) {
						try {
							pool.execute(new Mouse(curr.move(Direction.NORTH),
									DistanceCount + 1, myTree.newNorthTree()));
						} catch (RejectedExecutionException e) {
							return;
						}
						count--;
						canNorth = false;
					} else if (canWest) {
						try {
							pool.execute(new Mouse(curr.move(Direction.WEST),
									DistanceCount + 1, myTree.newWestTree()));
						} catch (RejectedExecutionException e) {
							return;
						}
						count--;
						canWest = false;
					} else if (canEast) {
						try {
							pool.execute(new Mouse(curr.move(Direction.EAST),
									DistanceCount + 1, myTree.newEastTree()));
						} catch (RejectedExecutionException e) {
							return;
						}
						count--;
						canEast = false;
					}
				}
				if (canSouth) {
					myTree = myTree.newSouthTree();
					last = Direction.SOUTH;
					curr = curr.move(Direction.SOUTH);
				} else if (canWest) {
					myTree = myTree.newWestTree();
					last = Direction.WEST;
					curr = curr.move(Direction.WEST);
				} else if (canNorth) {
					myTree = myTree.newNorthTree();
					last = Direction.NORTH;
					curr = curr.move(Direction.NORTH);
				} else if (canEast) {
					myTree = myTree.newEastTree();
					last = Direction.EAST;
					curr = curr.move(Direction.EAST);
				}
				DistanceCount += 1;
			} catch (NullPointerException e) {
				return;
			}
		}

		private void solutionFound() {
			mySol.setSoln(myTree.getSol());

			pool.shutdown();
		}

		public void run() {
			// System.out.println("I have started");
			try {
				if (last == null) {
					// First time only
					doFirst();
				}
				while (true) {
					// System.out.println("currentThread: "+
					// Thread.currentThread().getName());
					byte count = 0;
					boolean canNorth = false;
					boolean canSouth = false;
					boolean canEast = false;
					boolean canWest = false;
					if (!last.equals(Direction.SOUTH)
							&& maze.canMove(curr, Direction.NORTH)) {
						if (curr.move(Direction.NORTH).equals(endLoc)) {
							myTree = myTree.newNorthTree();
							solutionFound();
							return;
						}
						count++;
						canNorth = true;
					}
					if (!last.equals(Direction.NORTH)
							&& maze.canMove(curr, Direction.SOUTH)) {
						if (curr.move(Direction.SOUTH).equals(endLoc)) {
							myTree = myTree.newSouthTree();
							solutionFound();
							return;
						}
						count++;
						canSouth = true;
					}
					if (!last.equals(Direction.WEST)
							&& maze.canMove(curr, Direction.EAST)) {
						if (curr.move(Direction.EAST).equals(endLoc)) {
							myTree = myTree.newEastTree();
							solutionFound();
							return;
						}
						count++;
						canEast = true;
					}
					if (!last.equals(Direction.EAST)
							&& maze.canMove(curr, Direction.WEST)) {
						if (curr.move(Direction.WEST).equals(endLoc)) {
							myTree = myTree.newWestTree();
							solutionFound();
							return;
						}
						count++;
						canWest = true;
					}
					if (count == 0) {
						return;
					}
					while (count > 1) {
						if (canNorth) {
							try {
								pool.execute(new Mouse(curr
										.move(Direction.NORTH),
										DistanceCount + 1, myTree
										.newNorthTree()));
							} catch (RejectedExecutionException e) {
								return;
							}
							count--;
							canNorth = false;
						} else if (canEast) {
							try {
								pool.execute(new Mouse(curr
										.move(Direction.EAST),
										DistanceCount + 1, myTree.newEastTree()));
							} catch (RejectedExecutionException e) {
								return;
							}
							count--;
							canEast = false;
						} else if (canWest) {
							try {
								pool.execute(new Mouse(curr
										.move(Direction.WEST),
										DistanceCount + 1, myTree.newWestTree()));
							} catch (RejectedExecutionException e) {
								return;
							}
							count--;
							canWest = false;
						}
					}
					if (canSouth) {
						myTree = myTree.newSouthTree();
						last = Direction.SOUTH;
						curr = curr.move(Direction.SOUTH);
					} else if (canWest) {
						myTree = myTree.newWestTree();
						last = Direction.WEST;
						curr = curr.move(Direction.WEST);
					} else if (canNorth) {
						myTree = myTree.newNorthTree();
						last = Direction.NORTH;
						curr = curr.move(Direction.NORTH);
					} else if (canEast) {
						myTree = myTree.newEastTree();
						last = Direction.EAST;
						curr = curr.move(Direction.EAST);
					}
					DistanceCount++;
				}
			} catch (NullPointerException e) {
				return;
			}
		}

		public int compareTo(Mouse o) {
			return o.DistanceCount - DistanceCount;
		}
	}

	private class getSolution {
		public boolean isDone = false;
		private List<Direction> solution = null;
		public Object doneNotify = new Object();

		public List<Direction> getSoln() {
			if (isDone) {
				return solution;
			} else {
				return null;
			}
		}

		public void setSoln(List<Direction> sol) {
			// java.util.Collections.reverse(sol);
			synchronized (doneNotify) {

				solution = sol;
				isDone = true;
				doneNotify.notifyAll();
			}
		}
	}

	private class SolutionTree {
		private SolutionTree parent;
		public Direction pathFromParent;
		private SolutionTree east;
		private SolutionTree west;
		private SolutionTree north;
		private SolutionTree south;

		public SolutionTree(SolutionTree p) {
			parent = p;
		}

		public SolutionTree newNorthTree() {
			north = new SolutionTree(this);
			north.pathFromParent = Direction.NORTH;
			return north;
		}

		public SolutionTree newSouthTree() {
			south = new SolutionTree(this);
			south.pathFromParent = Direction.SOUTH;
			return south;
		}

		public SolutionTree newWestTree() {
			west = new SolutionTree(this);
			west.pathFromParent = Direction.WEST;
			return west;
		}

		public SolutionTree newEastTree() {
			east = new SolutionTree(this);
			east.pathFromParent = Direction.EAST;
			return east;
		}

		public LinkedList<Direction> getSol() {
			SolutionTree curr = this;
			LinkedList<Direction> returnVal = new LinkedList<Direction>();
			while (curr.pathFromParent != null) {
				returnVal.addFirst(curr.pathFromParent);
				curr = curr.parent;
			}
			return returnVal;
		}
	}
}
