# Taken from https://www.tensorflow.org/tutorials/text/text_generation

import tensorflow as tf
import numpy as np
import os
import time

CHECKPOINT_DIR = 'C:\\temp\\training_checkpoints'

text = open("lovecraft.txt", 'rb').read().decode(encoding='utf-8')
vocab = sorted(set(text))
VOCAB_SIZE = len(vocab)
char2idx = { u:i for i, u in enumerate(vocab) }
idx2char = np.array(vocab)
text_as_int = np.array([char2idx[c] for c in text])


def build_model(batch_size):
    EMBEDDING_DIM = 256
    RNN_UNITS = 1024

    return tf.keras.Sequential([
        tf.keras.layers.Embedding(VOCAB_SIZE, EMBEDDING_DIM,
                                  batch_input_shape=[batch_size, None]),
        tf.keras.layers.GRU(RNN_UNITS,
                            return_sequences=True,
                            stateful=True,
                            recurrent_initializer='glorot_uniform'),
        tf.keras.layers.Dense(VOCAB_SIZE)
      ])


def train():
    seq_length = 100
    examples_per_epoch = len(text)//(seq_length+1)
    char_dataset = tf.data.Dataset.from_tensor_slices(text_as_int)
    sequences = char_dataset.batch(seq_length+1, drop_remainder=True)
    dataset = sequences.map(lambda chunk: (chunk[:-1], chunk[1:]))

    BUFFER_SIZE = 10000
    BATCH_SIZE = 64
    dataset = dataset.shuffle(BUFFER_SIZE).batch(BATCH_SIZE, drop_remainder=True)
    model = build_model(batch_size=BATCH_SIZE)

    def loss(labels, logits):
        return tf.keras.losses.sparse_categorical_crossentropy(labels, logits, from_logits=True)

    model.compile(optimizer='adam', loss=loss)
    checkpoint_prefix = os.path.join(CHECKPOINT_DIR, "ckpt_{epoch}")
    checkpoint_callback=tf.keras.callbacks.ModelCheckpoint(
        filepath=checkpoint_prefix,
        save_weights_only=True)
    EPOCHS = 30
    history = model.fit(dataset, epochs=EPOCHS, callbacks=[checkpoint_callback])


def generate(start_string):
    model = build_model(batch_size=1)
    model.load_weights(CHECKPOINT_DIR + '\ckpt_30')
    model.build(tf.TensorShape([1, None]))
    model.reset_states()
    
    num_generate = 1000
    input_eval = [char2idx[s] for s in start_string]
    input_eval = tf.expand_dims(input_eval, 0)

    text_generated = []
    temperature = 1.0

    
    for i in range(num_generate):
        predictions = model(input_eval)
        # remove the batch dimension
        predictions = tf.squeeze(predictions, 0)

        # using a categorical distribution to predict the character returned by the model
        predictions = predictions / temperature
        predicted_id = tf.random.categorical(predictions, num_samples=1)[-1,0].numpy()

        # We pass the predicted character as the next input to the model
        # along with the previous hidden state
        input_eval = tf.expand_dims([predicted_id], 0)
        text_generated.append(idx2char[predicted_id])

    return (start_string + ''.join(text_generated))

train()
print(generate("Horror"))