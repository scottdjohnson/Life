/****************************************

	CircularBuffer.java 

	This class is a circular buffer, implemented as a static stack which can wrap around 
		the end and dynamically move the bottom location of the stack.

	The contents are overwritten after "size" objects are pushed in, but as many as 
		"size" - 1 objects remain available to be popped off and the extra cell is 
		marked as a null to signify the "bottom" of the stack/buffer. 

	We start with index[0] as the origin, but always put a null in 
		the next index after we push so that if we have wrapped all the way around we 
		will know to stop there.

	An empty buffer is one in which the current index is null. 		
	
****************************************/

public class CircularBuffer
{

	private Object [] buffer;
	private int index;

    	public CircularBuffer(int capacity)
	{
        	buffer = new Object[capacity];
        	index = 0;
		buffer[index] = null;
    	}

    	public void push(Object i)
	/*
		Put a new item in the next location, put a null marker
		in the following location.
	*/
	{
		index = nextIndex();
		buffer[index] = i;
		buffer[nextIndex()] = null;
    	}

    	public Object pop()
	/*
		Pop the item from the current index
	*/
	{
	
		Object ret = buffer[index];

        	if(ret != null)
			index = prevIndex();

		return ret;
	}

	private int nextIndex()
	// Find the next index, wrap around if necessary
	{
		int tmp = index + 1;
		
		// If we are pointing at the end, "next" is now the first item
		if (buffer.length == tmp)
			tmp = 0;
		
		return tmp;
	}

	private int prevIndex()
	// Find the previous index, wrap around if necessary
	{
		int tmp = index - 1;

		// Point to the previous index, or the end if we are at the front
		if (0 > tmp)
			tmp = buffer.length - 1;
			
		return tmp;
	}
}