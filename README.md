# smallmeeting
A tiny framework for TCP,UDP,NIO development on android project.

### 这是一个为了Android局域网Socket通讯而建立的一个小框架。目前有UPD广播和NIO TCP两种方法。

#### 下面是一个NIO byte流通讯的例子
> 服务端

		//数据接收
		NioReceiver nr = new NioReceiver() {
			
			@Override
			public int receive(SocketChannel socketChannel) throws IOException {
				ByteBuffer bb = ByteBuffer.allocate(1024);
				socketChannel.read(bb);
				bb.flip();
				byte[] buffer = new byte[bb.limit()];
				bb.get(buffer);
				System.out.println("收到客户端的数据:"+Arrays.toString(buffer));
				return buffer.length;
			}
		};
		NioServer<byte[]> server = new NioServer<>(nr);
		//服务启动
		server.start();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				if ("exit".equals(line)) {
					server.stop();
					break;
				} else {
					//给所有client发送数据
					server.sendAll(ByteBuffer.wrap(new byte[]{0,1,2,3}));
				}
			}
			Logger.d("over");
		} catch (IOException e) {
			e.printStackTrace();
		}

> 客户端

		NioReceiver nr = new NioReceiver() {
			
			@Override
			public int receive(SocketChannel socketChannel) throws IOException {
				ByteBuffer bb = ByteBuffer.allocate(1024);
				socketChannel.read(bb);
				bb.flip();
				byte[] buffer = new byte[bb.limit()];
				bb.get(buffer);
				System.out.println("收到服务端的数据:"+Arrays.toString(buffer));
				return buffer.length;
			}
		};
		
		final NioClient<byte[]> client = new NioClient<>("localhost", 19420, nr);

		client.start();

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				if ("exit".equals(line)) {
					client.stop();
					break;
				} else {
					client.send(ByteBuffer.wrap(new byte[]{0,1,2,3}));
				}
			}
			Logger.d("over");
		} catch (IOException e) {
			e.printStackTrace();
		}

数据的格式有多种。传输的数据量不能超过指定缓冲区的大小，你可以通过下面的方式来指定缓冲区和获取的大小

> 服务端

	public int getRead_buffer_size() {
		return read_buffer_size;
	}

	public void setRead_buffer_size(int read_buffer_size) {
		this.read_buffer_size = read_buffer_size;
		if (receiver != null) {
			receiver.setReadBufferSize(read_buffer_size);
		}
	}

> 客户端

	public int getRead_buffer_size() {
		return read_buffer_size;
	}

	public void setRead_buffer_size(int read_buffer_size) {
		this.read_buffer_size = read_buffer_size;
		if (receiver != null) {
			receiver.setReadBufferSize(read_buffer_size);
		}
	}

