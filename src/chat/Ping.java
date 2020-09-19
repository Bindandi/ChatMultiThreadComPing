/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

/**
 *
 * @author Julio Bindandi
 */
public class Ping {
   private ChatClientHandlerListener listener;
   private Socket socket;
   private String username;
   private PrintWriter output;
    
   private static final int QTDPACOTESENVIO = 1000;
        
   private LocalTime horaEnvio = null;
   private LocalTime horaRecebimento = null;
   private long tempoTotal;
   private long tempoMedio;
   private long tempoMenor;
   private long tempoMaior;
   private int qtdPacotesRecebidos; 
   
   public Ping(ChatClientHandlerListener listener, Socket socket, String username)
   {
       this.listener = listener;
       this.socket = socket;
       this.username = username;
       tempoTotal = 0;
       tempoMaior = 0;
       tempoMenor = 0;
       tempoMedio = 0;
       qtdPacotesRecebidos = 0;
   }
   
   public void executar()
   {
        try  {
            output = new PrintWriter(socket.getOutputStream()); 
            for(int i =0; i < QTDPACOTESENVIO; i++)
            {
                horaEnvio = LocalTime.now();
                listener.ping(username);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
       
   }
   
   public void setRetorno()
   {
        qtdPacotesRecebidos++;
        
        horaRecebimento = LocalTime.now();
            
        long nano = ChronoUnit.NANOS.between(horaEnvio, horaRecebimento);
            
        tempoTotal = tempoTotal + nano;
        tempoMedio = tempoTotal / qtdPacotesRecebidos;
            
        if(nano > tempoMaior)
            tempoMaior = nano;
        if(qtdPacotesRecebidos == 1 || nano < tempoMenor)
            tempoMenor = nano;
        
        if(qtdPacotesRecebidos == QTDPACOTESENVIO)
            finalizar();
   }
   
   private void finalizar()
   {
        output.println("Tempo Total: "+ tempoTotal + " ns");
        output.println("Tempo Maior: "+ tempoMaior + " ns");
        output.println("Tempo Menor: "+ tempoMenor + " ns");
        output.println("Tempo Medio: "+ tempoMedio + " ns");
        output.flush();
        
        listener.end(username); //creio que com esta abordagem nao fez muito sentido 
                                //matar a conexao, porem segui o escopo.
   }
}
