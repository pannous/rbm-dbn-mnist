package com.pannous.rbm;

public  class State
{
    public final Layer visible;
    public final Layer hidden;
    public final Layer input;   //For a DBN this is the initial input layer

    public State(Layer input, Layer visible, Layer hidden)
    {
        this.input = input;
        this.visible = visible;
        this.hidden = hidden;
    }

    public static class Factory {

        public final Layer input;

        public Factory(Layer input) {
            this.input = input;
        }

        public State create(Layer visible, Layer hidden)
        {
            return new State(input,visible,hidden);
        }
    }
}
