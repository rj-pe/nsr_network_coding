# Changelog
All notable changes to this project will be documented in this file.
The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),

## [Unreleased]
### Fixed
- Fixed ``instantiate_network_layers()`` method in ``Topology.java`` class. 
An indexing error was causing the first layer of intermediate nodes to be skipped.
### Added
- Added edge case handling to address issue #7 .
During Gaussian elimination any rows which are not encoded are skipped.
- Added a parent class ``IntermediateNode.java``.
Both the production and debug versions of the code inherit from parent.
- Added network parameters in `.log` file. The dynamic network parameters that specify the 
topology are logged.
- Added classes that enable specification of an arbitrary cascading network topology.
New classes, ``Topology.java`` and ``Layer.java`` implement cascading network topology functionality.
Each class includes full Javadoc documentation. 
- Added ``.gitignore`` file.
### Changed
- Constructor for ``Node``.
New constructor requires that the network minimum cut be specified when creating new nodes.
- Names for intermediate nodes.
New intermediate node names are formatted as follows: 
intermediate node ``n`` in layer ``l`` is named ``(l, n)``.
- Reduced logger verbosity.
Removed the level tag in ``logger.properties``. This change makes `.log` files easier to parse by the 
testing framework. 
- Changed ``.log`` to comma separated values format. This change allows harvesting of log data using 
standard CSV style.


## [0.2.0](https://github.com/rj-pe/nsr_network_coding/tree/v0.2.0/src) - 2018-11-24
### Added
- Added ``CHANGELOG.md``. This file will document all project changes.
- Added a developer mode. 
This change facilitates running the app with hardcoded network parameters.
Developer mode can be set in App.java with the boolean ``DEV_MODE``. Currently the network parameters
that can be hardcoded are local encoding vectors at the intermediate nodes and the 
packet length field of the sender node. New sender node debug and intermediate node debug classes
were implemented to maintain a code structure consistent with OOP principles.

### Fixed
- Fixed a bug in a method, ``convertToLeadingNonZero()``, which is used by the Gaussian elimination algorithm.
This change ensures that matrix row-swapping occurs. Java's ``Collections.swap()`` method is used.

## [0.1.0](https://github.com/rj-pe/nsr_network_coding/tree/v0.1.0/src) - 2018-11-24
### Added
- Added logging.
Logging implemented with Java's basic logging API ``java.util.logging``. Network parameters and any
runtime exceptions are logged to a file ``name_of_packet.log``. The ``logging.properties`` 
file is a dependency.
- Added inline documentation to source code. 
Documentation using Javadoc formatting is ongoing. For now inline comments that describe major
operations in the existing code have been added. Next steps are to organize these comments into Javadoc style.
### Changed
- Program uses the hexstream packet format. 
Originally the program parsed packet data with the assumption that the data was formatted using a 
delimiter ``|``. This parsing was removed as packets copied from Wireshark do not contain this delimiter. 
### Fixed
- Fixed a bug in method, ``readData()``. 
Originally only data that fit into a single generation was being sent through the network. 
The method was fixed to include the entire contents of the original packet, irrespective of the 
number of generations required to broadcast a packet through network.
- Fixed a bug in class ``IntermediateNodeProduction``, method ``handle()``. 
The bug occurs because sink node, t1 modifies each intermediate node’s copy of the outgoing packet. 
Because of the sequential nature of the program logic, sink node t2 starts to decode only after t1 is 
completely finished decoding. Because the sink nodes were not getting their own copy of the 
packets sent from the intermediate nodes, when t2 tried to decode, it was essentially 
operating on a plain-text version of the message. Fixing the bug was much easier than finding it, 
as all that was needed was to send a clone of the packet rather than a reference to it.
- Fixed ``topology.jpg``. 
This image is a simplified example of the original application's topology and encoding.
The image did not accurately depict the packets that were being sent by the intermediate nodes. The 
image was updated to accurately portray the encoding that was taking place at the intermediate nodes.
