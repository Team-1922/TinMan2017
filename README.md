# TinMan2016
The year the code works

###Configuring workspace
To use this code in eclipse, make sure you have the c++ version of eclipse installed with the wpilib toolchain installed.  The instructions for this can be found on screestepslive.  

To start, create a eclipse workspce, or use a current one.  Create a new Java Robot project.  It is easiest to use the "simple" preset, becuase there are fewer files to delete.  Delete the src folder.  Add a new src folder to the project, but link it to the src folder in the git repository.  Then right click the project and add a new source folder, pointing to the src folder.

open `build.xml` in the eclipse project, and replace all of the text with the text in `build.xml` found in the root of the git repo.  Replace "PATH_TO_REPO" with the relative or absolute path to the root of the repo.


###Deploying without Running Eclipse
Sometimes it might be useful to deploy without having to open eclipse.  Eclipse must still be installed, or else ant would not work, however loading times might be invonvenient.
Simply run `Deploy.bat` or `ReUploadCfg.bat`

###Coms with Computer and PI
By default, the ip address of the driver station computers, or the pi are on DHCP, but to get comms, they must be on static IP mode.  This can be acieved on windows by doing network and sharing center -> Ethernet -> ipv4 -> static IP: (10.TE.AM.anything), mask: (255.0.0.0).  On PI: open /etc/network/interfaces and replace all parts of the file which reference "eth0" with :
`auto eth0`
`allow-hotplug eth0`
`iface eth0 inet static`
`address 10.TE.AM.whatever`
`netmask 255.0.0.0`
`network 10.TE.AM.0`
