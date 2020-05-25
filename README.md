# Goal
To generate a typical Lovecraft story given collection of his stories.

# Approach
The first thing I've tried is sort of Markov chain - for each word I calculate a chance of each subsequent word and then use weighted random to generate a story word by word.

# Results so far
1. Markov chain
```
Ngranek and independent delvings had seized a distant undefinable foetor as they had made familiar arose from the rotting body retaliate. but back till finally they must be on the shape and I saw with the charred foundation walls and the stooping. This marked by the next to you very curious sort of us even to use to possess itself even this spectrum. We were too soon saw even these stubborn but it soon had dreaded the lower realm was something vaguely disturbed me. These dogs slavered and a red rays of the students all such documents and clerical costume one could scarcely knew too. Allen had of the hasty funeral how to hear their mad at the pastures edged cone shaped or of them along. After that dwells all skunk cabbages coming in its arms gave each day or may guess but as he had previously. When he was the stars came to light like one on the ghost stalked leeringly in speaking or dreadful and probably. Every attribute of perspective in Parg up this nighted mystery and produced Raeburn and more houses and giving me a de. I do elsewhere he will be led the oxy hydrogen blowpipe. two months certain information regarding the citadel background after dark ships of those of external resemblance to imaginings of immediate objective. As I was because of furtive and method would pinch experimentally were slightly proud and monkeys till it never seen would. It must pass that will ever try to see that this fabulous towers and he was there might rest is that. For eighty feet end he were marched up with sledges of the deserted edifice of ineffable foetor none of meat and. However the gates open with the wrong with their tramp not know why and embodied daemon wind stronger all as I. I was still on to watch and bones he could be looked from the vast gardens set my own intuitive knack. Freeborn Boyle Boerhaave Becher and evil one can not surprised response 'as indeed they can never made use of the town. Crime was altogether and son Alfred a local aquaintances he indulged in that silent heretofore recorded with materials carefully compiled record. precipitating as a ship at night the old Mr Bishop's cows had discovered why no vulgar ghouls eager for the clicking. No trials or Shirley or government made me nor Acids loth to send her and a piece by his fourth year.
```
1. RNN
```
Dunwich Victorian Land.

So the Shantak was a strange and abysmal sea of strange and the sounds in the sea and the sound of the strange philosophy came to the sea of strange and the sounds in the sea and the sound of the sounds in the sea and the sound of the sounds in the sea and the sound of the sounds in the sea and the sound of the sounds in the sea and the sound of the strange philosophy came to me the last of the Great Ones who had seen the things that had been the same when the strange bearded man spoke of the south window and the sound of the sounds in the sea and the sound of the sounds in the sea and the sound of the sounds in the sea and the sound of the sounds in the sea and the sound of the sounds in the sea and the sound of the sounds in the sea and the sound of the sounds in the sea and the sound of the sounds in the sea and the sound of the sounds in the sea and the sound of the sounds in the sea and the sound of the sounds in the sea and the sound of the sounds in the sea
``` 

# Plan
1. Install gpu libs
1. https://github.com/minimaxir/textgenrnn
1. Add another couple of RNN layers
1. Word-level generation in RNN 
1. Research memory 
