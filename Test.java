

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;


public class Test {
	public static void main(String [] args) {
		ExecutorService executorService = Executors.newCachedThreadPool();
		ReadWriteLock RW = new ReadWriteLock();
		
		
		executorService.execute(new Writer(RW));
		executorService.execute(new Writer(RW));
		executorService.execute(new Writer(RW));
		executorService.execute(new Writer(RW));
		
		executorService.execute(new Reader(RW));
		executorService.execute(new Reader(RW));
		executorService.execute(new Reader(RW));
		executorService.execute(new Reader(RW));


	}
}


class ReadWriteLock{
	private Semaphore S_resource = new Semaphore(1);// controlling acces to resource
	private Semaphore S_counter = new Semaphore(1);// controll access to counter
	private Semaphore S_queue = new Semaphore(1);//semaphore to prevent deadlock (take task from order)
	private volatile int reader_counter = 0;// number of readers
	
	    	
	public void readLock() throws InterruptedException{
		
		S_queue.acquire();// proccess waiting to be serviced
		S_counter.acquire();// request access to increase counter
		reader_counter++;
		if(reader_counter == 1)// if there is no reader proccess acquire resource otherwise not
		{
			S_resource.acquire();// acquire access if there is no writer
		}
		S_queue.release();// allow next reader to be serviced           
		S_counter.release();// release access to counter
		
	}
			
		
	public void writeLock() throws InterruptedException {
							
		S_queue.acquire();// wait in queue to be serviced            
		S_resource.acquire();// take exclusive access to resource               
		S_queue.release();// let next process to be serviced   		
	   	
	}
	
	
	public void readUnLock() throws InterruptedException{	
			
		S_counter.acquire();// acquire access to counter                 
		reader_counter--;               
		if(reader_counter == 0)// if no reader left release resource
		{
			S_resource.release();
		}
		S_counter.release();  // allow access to counter 
		
	}
	
	
	public void writeUnLock() {
		
		S_resource.release(); // release resource	
		
	}
}


class Writer implements Runnable
{ 
   private ReadWriteLock RW_lock;
   
    public Writer(ReadWriteLock rw) {
    	System.out.print("w\n");
    	RW_lock = rw;   	
   }

    public void run() {
      while (true){
    	  try {
			RW_lock.writeLock();
			System.out.println("writer strated");
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} 

    	  
    	  try {
    		  System.out.print("write\n");
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	  
    	  System.out.println("writer finished");
    	  RW_lock.writeUnLock(); //release lock
      }
   }

}



class Reader implements Runnable
{
   private ReadWriteLock RW_lock;
   

   public Reader(ReadWriteLock rw) {
    	RW_lock = rw;
    	System.out.print("r\n");
   }
    public void run() {
      while (true){ 
    	  
    	  try {
			RW_lock.readLock();
			System.out.print("read started\n");
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	  
    	  
    	  try {
  			Thread.sleep(1000);
  		} catch (InterruptedException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}
    	  
    	  
    	  try {
    		  System.out.println("reader finished");
			RW_lock.readUnLock();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //release lock
    	  
     	  
    	  
      }
   }


}






