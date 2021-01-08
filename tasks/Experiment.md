#Experiment guide

This document describes the creation of this task set and how to replicate the experiment that it was used for. 

### Motivation 
There are few controlled studies of how developers use refactoring tools during software change tasks. We created this task set to study how developers approach software change tasks that are amenable to refactoring tools. 

### Creation
We created a collection of software change scenarios that contain refactoring operations by mining commits from online software repositories using a [state-of-the-art tool](https://github.com/tsantalis/RefactoringMiner). We also examined commits that were collected in another [study of refactorings](https://aserg-ufmg.github.io/why-we-refactor/#/) that used a previous version of the same tool. 

From this pool, we selected commits that was aligned with the task size and motivation. We found several candidates, formulated preliminary task descriptions and performed pilot studies. Based on the pilots and our requirements, we decided to use a scaled-down version of one of the mined systems, [Apache Commons-Lang](https://github.com/apache/commons-lang) as the target system. Task one and two originated from their history, and was therefore easy to map onto the scaled-down system. Task three is from [Quasar](https://github.com/puniverse/quasar/) and was mapped onto this target system. 

### Participants 
In order to participate in our study, it was a requirement with one year of experience with Java programming. 17 participants fullfilled this criteria and two participants were admitted without this criteria due to extensive experience with other object-oriented languages. We performed 6 pilot study sessions and 19 participant study sessions. The data from 2 of our 19 participants were excluded due to technical problems with the IDE and inability to progress on tasks. Data from the remaining 17 participants form the basis for analysis in this paper.

### Study setup 
Participants were provided with the system found in the ./navajo-project folder loaded into a running instance of the IntelliJ IDE. The system also had a git repository initialised, which is not present in this repository. **In case of replication, please set up a git repository and add to it some history e.g. using empty commits. Inform participants of the existence of this repository during the intro segment.**

The screen and audio was recorded using the QuickTime media player that was present on the provided laptop. 

Participants followed a think-aloud protocol during the experiment session with the experimenter making prompts when they failed to do so. 

### Study execution 

Participants were given the Intro-sheet of the ./Tasks.pdf file and allowed up to ten minutes to familiarize themselves with the source code. During this time they were informed about the existence of the test code and the git repository and allowed to ask questions, set up keybindings, et.c. 

Then, they were given one task at a time in increasing order. The tasks are estimated to take around 10-15 minutes for the first task, 15-20 minutes for the second task, and 40-60 minutes for the third task. If participants reach the end of this time without completing a task, they should be prompted to continue to the next. If they complete a task before the end of this time, they should continue. Any time spent on "interview segments" *during the task* is deducted from this time. This happened when participants engaged in discussions during tasks that would otherwise occur in the final interview segment. 

Each task is performed on the same (incremental) codebase: i.e. task two is performed on the resulting code from task one. It is not necessary to solve the task correctly to continue to the next. If the code base was not compiling at this time, the experimenter helped the participant reach a compiling state before continuing. **In case of replication, we suggest creating three git branches with the starter code for task one (which is in this folder), for task two (after a correct solution of task one), and for task three (after a correct solution of task one and two). This would make it easier for participants with incomplete tasks.**

After each task, participants were asked the following questions: 

* What source code changes did you do in order to solve this task? 
* Are you aware of any tools that automate any of those changes? 
* Are there any changes you are unsure if you got right? 

After all three tasks were over, a semi-structured interview was conducted. During this session, participants were asked about their approaches, about refactoring tools that they had used or disused during the study and other experiences they have with refactoring tools. 
