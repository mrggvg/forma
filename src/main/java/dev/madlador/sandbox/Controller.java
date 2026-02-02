package dev.madlador.sandbox;

public class Controller {

    private Model model;
    private View view;

    public Controller(Model model) {
        this.model = model;
        this.view = new View(this, this.model);
    }

    public void incrementCount() {
        this.model.setCount(this.model.getCount() + 1);
    }

    public void decrementCount() {
        this.model.setCount(this.model.getCount() - 1);
    }

}
