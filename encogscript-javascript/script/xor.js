training = new TrainingData(2,1);
training.define(0,0,  0);
training.define(0,1,  1);
training.define(1,0,  1);
training.define(1,1,  0);

network = new NeuralNetwork();
network.createFeedForward(2,2,0,1,"sigmoid");

trainer = new Trainer();
trainer.create(network,training);
trainer.trainToError(0.01);
network.evaluate(training);