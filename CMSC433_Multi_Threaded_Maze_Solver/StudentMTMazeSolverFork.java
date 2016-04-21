package cmsc433.p3;

import java.util.LinkedList;
import java.util.List;
//import java.util.Stack;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionException;

public class StudentMTMazeSolverFork extends SkippingMazeSolver {

	private SolutionTree root;
	private BlockingDeque<SolutionTree> jobStack = new LinkedBlockingDeque<SolutionTree>();
	private Position endLoc;
	private getSolution mySol = new getSolution();
	private int jobsSubmitted = 0;
	private int jobsStarted = 0;
	private int jobsFinished = 0;

	public StudentMTMazeSolverFork(Maze maze) {
		super(maze);
		root = new SolutionTree(null, maze.getStart());
		endLoc = maze.getEnd();
	}

	private class SolutionTree{
		private SolutionTree parent;
		public Direction pathFromParent;
		private Position currLoc;
		private SolutionTree east;
		private SolutionTree west;
		private SolutionTree north;
		private SolutionTree south;

		public SolutionTree(SolutionTree p, Position loc){
			parent = p;
			currLoc = loc;
		}
		public SolutionTree newNorthTree(){
			north = new SolutionTree(this, currLoc.move(Direction.NORTH));
			north.pathFromParent = Direction.NORTH;
			return north;
		}public SolutionTree newSouthTree(){
			south = new SolutionTree(this, currLoc.move(Direction.SOUTH));
			south.pathFromParent = Direction.SOUTH;
			return south;
		}public SolutionTree newWestTree(){
			west = new SolutionTree(this, currLoc.move(Direction.WEST));
			west.pathFromParent = Direction.WEST;
			return west;
		}public SolutionTree newEastTree(){
			east = new SolutionTree(this, currLoc.move(Direction.EAST));
			east.pathFromParent = Direction.EAST;
			return east;
		}

		public LinkedList<Direction> getSol(){
			SolutionTree curr = this;
			LinkedList<Direction> returnVal = new LinkedList<Direction>();
			while(curr.pathFromParent != null){
				returnVal.addFirst(curr.pathFromParent);
				curr = curr.parent;
			}
			return returnVal;
		}
	}

	private class mouse implements Runnable{
		SolutionTree curr;
		SolutionTree lastBranch;
		private void solutionFound(){
			mySol.setSoln(curr.getSol());
		}

		private void doFirst(){
			byte count = 0;
			boolean canNorth = false;
			boolean canSouth = false;
			boolean canEast = false;
			boolean canWest = false;
			Position currLoc = curr.currLoc;
			if(maze.canMove(currLoc, Direction.NORTH)){
				if(currLoc.move(Direction.NORTH).equals(endLoc)){
					curr = curr.newNorthTree();
					solutionFound();
					return;
				}
				count++;
				canNorth = true;
			}
			if(maze.canMove(currLoc, Direction.SOUTH)){
				if(currLoc.move(Direction.SOUTH).equals(endLoc)){
					curr = curr.newSouthTree();
					solutionFound();
					return;
				}
				count++;
				canSouth = true;
			}
			if(maze.canMove(currLoc, Direction.EAST)){
				if(currLoc.move(Direction.EAST).equals(endLoc)){
					curr= curr.newEastTree();
					solutionFound();
					return;
				}
				count++;
				canEast = true;
			}
			if(maze.canMove(currLoc, Direction.WEST)){
				if(currLoc.move(Direction.WEST).equals(endLoc)){
					curr = curr.newWestTree();
					solutionFound();
					return;
				}
				count++;
				canWest = true;
			}
			if(count == 0){
				if(lastBranch == null){
					curr = lastBranch;
					lastBranch = null;
				}else{
					synchronized(jobStack){
						jobsFinished++;
						return;
					}
				}
			}
			while(count > 1){
				if(canNorth){
					if(lastBranch == null){
						lastBranch = curr.newNorthTree();
					}else{
						try{
							synchronized(jobStack){
								jobsSubmitted++;
								jobStack.push(lastBranch);
								jobStack.notifyAll();
							}
							lastBranch = curr.newNorthTree();
						}catch(RejectedExecutionException e){
							return;
						}
					}
					count--;
					canNorth = false;
				}else if(canEast){
					if(lastBranch == null){
						lastBranch = curr.newEastTree();
					}else{
						try{
							synchronized(jobStack){
								jobsSubmitted++;
								jobStack.push(lastBranch);
								jobStack.notifyAll();
							}
							lastBranch = curr.newEastTree();
						}catch(RejectedExecutionException e){
							return;
						}
					}
					count--;
					canEast = false;
				}else if(canWest){
					if(lastBranch == null){
						lastBranch = curr.newWestTree();
					}else{
						try{
							synchronized(jobStack){
								jobsSubmitted++;
								jobStack.push(lastBranch);
								jobStack.notifyAll();
							}
							lastBranch = curr.newWestTree();
						}catch(RejectedExecutionException e){
							return;
						}
					}
					count--;
					canWest= false;
				}
			}
			if(canSouth){
				curr = curr.newSouthTree();
			}else if(canWest){
				curr = curr.newWestTree();
			}else if(canNorth){
				curr = curr.newNorthTree();
			}else if(canEast){
				curr = curr.newEastTree();
			}
		}

		public void run() {
			while(true){
				System.out.println("JobsStarted: " +jobsStarted
						+"JobsSubmitted: " + jobsSubmitted
						+"JobsFinished: " + jobsFinished);
				boolean notDeadEnd = true;
				synchronized(jobStack){
					while(jobStack.isEmpty()){
						try {
							jobStack.wait();
						} catch (InterruptedException e) {
							return;
						}
					}
					curr =jobStack.pop();
					jobsStarted++;
				}
				while(notDeadEnd){
					if(curr.pathFromParent == null){
						doFirst();
						continue;
					}
					byte count = 0;
					boolean canNorth = false;
					boolean canSouth = false;
					boolean canEast = false;
					boolean canWest = false;
					Position currLoc = curr.currLoc;
					if(!curr.pathFromParent.equals(Direction.SOUTH) && maze.canMove(currLoc, Direction.NORTH)){
						if(currLoc.move(Direction.NORTH).equals(endLoc)){
							curr = curr.newNorthTree();
							solutionFound();
							return;
						}
						count++;
						canNorth = true;
					}
					if(!curr.pathFromParent.equals(Direction.NORTH) && maze.canMove(currLoc, Direction.SOUTH)){
						if(currLoc.move(Direction.SOUTH).equals(endLoc)){
							curr = curr.newSouthTree();
							solutionFound();
							return;
						}
						count++;
						canSouth = true;
					}
					if(!curr.pathFromParent.equals(Direction.WEST) && maze.canMove(currLoc, Direction.EAST)){
						if(currLoc.move(Direction.EAST).equals(endLoc)){
							curr= curr.newEastTree();
							solutionFound();
							return;
						}
						count++;
						canEast = true;
					}
					if(!curr.pathFromParent.equals(Direction.EAST) && maze.canMove(currLoc, Direction.WEST)){
						if(currLoc.move(Direction.WEST).equals(endLoc)){
							curr = curr.newWestTree();
							solutionFound();
							return;
						}
						count++;
						canWest = true;
					}
					if(count == 0){
						if(lastBranch != null){
							curr = lastBranch;
							lastBranch = null;
						}else{
							synchronized(jobStack){
								notDeadEnd = false;
								jobsFinished++;
								if(jobsFinished==jobsStarted && jobsStarted == jobsSubmitted){
									mySol.setSoln(null);
								}
							}
						}
						continue;
					}
					while(count > 1){
						if(canNorth){
							if(lastBranch == null){
								lastBranch = curr.newNorthTree();
							}else{
								try{
									synchronized(jobStack){
										jobsSubmitted++;
										jobStack.push(lastBranch);
										jobStack.notifyAll();
									}
									lastBranch = curr.newNorthTree();
								}catch(RejectedExecutionException e){
									return;
								}
							}
							count--;
							canNorth = false;
						}else if(canEast){
							if(lastBranch == null){
								lastBranch = curr.newEastTree();
							}else{
								try{
									synchronized(jobStack){
										jobsSubmitted++;
										jobStack.push(lastBranch);
										jobStack.notifyAll();
									}
									lastBranch = curr.newEastTree();
								}catch(RejectedExecutionException e){
									return;
								}
							}
							count--;
							canEast = false;
						}else if(canWest){
							if(lastBranch == null){
								lastBranch = curr.newWestTree();
							}else{
								try{
									synchronized(jobStack){
										jobsSubmitted++;
										jobStack.push(lastBranch);
										jobStack.notifyAll();
									}
									lastBranch = curr.newWestTree();
								}catch(RejectedExecutionException e){
									return;
								}
							}
							count--;
							canWest= false;
						}
					}
					if(canSouth){
						curr = curr.newSouthTree();
					}else if(canWest){
						curr = curr.newWestTree();
					}else if(canNorth){
						curr = curr.newNorthTree();
					}else if(canEast){
						curr = curr.newEastTree();
					}
				}
			}
		}
	}

	public List<Direction> solve(){	
		jobStack.push(new SolutionTree(null, maze.getStart()));
		jobsSubmitted++;
		int maxThreads = Runtime.getRuntime().availableProcessors();
		Thread[] threadList = new Thread[maxThreads];
		for(int itt = 0; itt < maxThreads; itt++){
			threadList[itt] = new Thread(new mouse());
			threadList[itt].setDaemon(true);
		}
		for(int itt = 0; itt < maxThreads; itt++){
			threadList[itt].start();
		}
		synchronized(mySol.doneNotify){
			while(!mySol.isDone){
				try {
					mySol.doneNotify.wait();
				} catch (InterruptedException e) {
					return mySol.getSoln();
				}
				return mySol.getSoln();
			}
		}
		return null;
	}

	private class getSolution{
		public boolean isDone = false;
		private List<Direction> solution = null;
		public Object doneNotify = new Object();
		public List<Direction> getSoln(){
			if(isDone){
				return solution;
			}else{
				return null;
			}
		}

		public void setSoln(List<Direction> sol){
			synchronized(doneNotify){
				solution = sol;
				isDone = true;
				doneNotify.notifyAll();
			}
		}
	}
}
