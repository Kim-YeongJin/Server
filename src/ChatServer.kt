import java.net.SocketAddress

fun main() {
    ChatServer()
}

class ChatServer : MacheteServer("127.0.0.1:3306/chat" , "root" , "0523") {
    override fun qaddRoom() {
    }

    override fun sopoOpen(ara: SocketAddress, protocol: Int, sopo: Array<Any>) {
    }

    override fun sopoProtocol(ara: SocketAddress, protocol: Int) {
    }

    override fun usrKick(ara: SocketAddress) {
    }

    override fun wrErrLog(ara: SocketAddress, key: Int, code: Int) {
    }

    override fun wrLog(ara: SocketAddress, key: Int, protocol: Int) {
    }
}