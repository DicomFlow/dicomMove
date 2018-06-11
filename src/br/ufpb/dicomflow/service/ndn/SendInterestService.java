package br.ufpb.dicomflow.service.ndn;

import java.io.IOException;
import java.nio.ByteBuffer;

import net.named_data.jndn.Data;
import net.named_data.jndn.Face;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import net.named_data.jndn.OnData;
import net.named_data.jndn.OnTimeout;
import net.named_data.jndn.encoding.EncodingException;

public class SendInterestService implements SendInterestServiceIF{
	
	private int maxAttempts;
	
	class Counter implements OnData, OnTimeout {
		
		public int callbackCount_ = 0;
		public String return_ = "";
		
		public void onData(Interest interest, Data data) {
			++callbackCount_;
			System.out.println("Got data packet with name " + data.getName().toUri());
			ByteBuffer content = data.getContent().buf();
			
			for (int i = content.position(); i < content.limit(); ++i)
				return_ += content.get(i);
		}

		public void onTimeout(Interest interest) {
			++callbackCount_;
			System.out.println("Time out for interest " + interest.getName().toUri());
			return_ = "";
		}
		
	}

	public String processInterest(String uri) {
		
			Face face = new Face();

			Counter counter = new Counter();

			// Try to fetch anything.
			Name name = new Name(uri);
			System.out.println("Express name " + name.toUri());
			try {
				face.expressInterest(name, counter, counter);
				face.processEvents();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (EncodingException e) {
				e.printStackTrace();
			}
			
		return counter.return_;

	}

	public int getMaxAttempts() {
		return maxAttempts;
	}

	public void setMaxAttempts(int maxAttempts) {
		this.maxAttempts = maxAttempts;
	}
	
	

}
