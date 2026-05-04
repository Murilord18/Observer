package banco.observer;


public interface Observable {


    void adicionarObserver(Observer observer);


    void removerObserver(Observer observer);


    void notificarObservers(String evento, double valor);
}
