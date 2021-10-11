

# Installation of the Intuitiv project (including Vonda for Rolli compilation)

    #install apache thrift (version 0.12.0 or higher) on your machine
    #for linux:


    git clone https://github.com/bkiefer/vonda.git
    cd vonda
    git checkout developer
    ./install_locallibs.sh
    mvn clean install
    # make sure bin/vondac is in PATH

    cd..
    git clone git@mlt-gitlab.sb.dfki.de:willms/drz_sign_of_life_module.git
    cd drz_sign_of_life_module
    #download and unzip dependencies
    gdown https://drive.google.com/uc\?id\=1eZIN8uHyr8VBTkKZVEh6ssviZ0wSD9Wf
    unzip common_lib.zip
    rm common_lib.zip
    # install dependencies
    sh install.sh
    #Test
    sh ./compile
    ./run.sh


## Flowchart
https://docs.google.com/drawings/d/1mVwPb0KXReamQLZdHwgrzS1sQQsl77gXkGlzxrRdSRQ/edit?usp=sharing


## Testing dialogues

First compile all rules `sh ./compile`, then start the test interface using `./run.sh`.
Enter the following statement into the test interface:

    Hallo

Now you should be greeted back by the system using an appropriate statement, such as

    Hallo
    Mahlzeit
    Guten Abend

You can also use the command `testDias()` in the userInterface to print out the realisations of all
emitDA statements in test.rudi


##[Dialogue acts provided by the NLU](./doc/dialogueActs.md)

## Docker Images
To start the Vonda Docker run

     docker-compose up --build

if you encounter any errors related to X11 try to run `xhost +"local:docker@"` on your host system
