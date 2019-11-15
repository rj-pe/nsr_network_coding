import csv
import os
import subprocess
import difflib
import time
import datetime
import glob
import shutil

intermediate_node_dict = {}
sink_node_list = []
log_to_list = []
num_layers = 0
nodes_per_layer = 0
num_sink_nodes = 0
num_intermediate_nodes = 0
network_min_cut = 0


def clean_dir(path):
    if not sink_node_list:
        for text_file in glob.glob("*.txt"):
            os.remove(text_file)
        return
    for sink in sink_node_list:
        sink_file = path + "/" + str(sink) + "-output.txt"
        try:
            os.remove(sink_file)
        except OSError as e:
            print("Error: %s - %s." % (e.filename, e.strerror))


def transfer_java_log(test_log_file_name):
    # write network parameters to test log
    test_log_file_name.write(
        "intermediate nodes per layer: {}, number of sink nodes: {}, number of layers: {}.\n"
        .format(nodes_per_layer, num_sink_nodes, num_layers))
    for n, v in intermediate_node_dict.items():
        test_log_file_name.write("{} {}\n".format(n, v))


def chunks(l, n):
    """Yield successive n-sized chunks from l."""
    for i in range(0, len(l), n):
        yield l[i:i + n]


def get_network_parameters(log_file):
    with open(log_file) as csv_file:
        global log_to_list
        csv_reader = csv.reader(csv_file, delimiter=';')
        log_to_list = list(csv_reader)

        # store network parameters
        global num_layers
        global nodes_per_layer
        global num_sink_nodes
        global num_intermediate_nodes
        global network_min_cut
        num_layers = int(log_to_list[2][0])
        nodes_per_layer = int(log_to_list[2][1])
        num_sink_nodes = int(log_to_list[2][2])
        network_min_cut = num_layers + 1

        # store intermediate node labels
        num_intermediate_nodes = num_layers * nodes_per_layer
        global intermediate_node_dict
        for i in range(4, 4 + num_intermediate_nodes):
            intermediate_node_dict[log_to_list[i][0]] = []

        # store intermediate node encoding vectors
        for i in range(4 + num_intermediate_nodes, len(log_to_list)):
            if log_to_list[i][0] in intermediate_node_dict:
                temp_list = [int(log_to_list[i][x]) for x in range(1, network_min_cut + 1)]
                intermediate_node_dict[log_to_list[i][0]] += temp_list

        # create sink node list
        for i in range(num_sink_nodes):
            global sink_node_list
            sink_node_list.append('t' + str(i))


# create timestamped files for the storage files that will hold the test results.
fname = datetime.datetime.utcfromtimestamp(time.time()).strftime("%y-%m-%d_%H:%M:%S")
# create a directories to hold test results
os.makedirs(fname)
log_dir = os.getcwd() + "/" + fname
os.makedirs(fname + "/results")

success_file = open(fname + "/results/" + fname + "_success", 'a')
exception_fail_file = open(fname + "/results/" + fname + "_exception_fail", 'a')
decode_fail_file = open(fname + "/results/" + fname + "_decode_fail", 'a')

fail_count = 0
pass_count = 0
exception_fail_count = 0
decode_fail_count = 0

packets_dir = os.getcwd() + "/packets"
java_app = "task1.jar"
os.chdir("app")
clean_dir(os.getcwd())

# loop through packets directory.
for packet in os.listdir(packets_dir):
    # run each packet through network.
    print("testing " + packet)
    # get info on test packet
    sent_packet = open(packets_dir + "/" + packet, 'r+').read()
    sent_packet_len = sum(len(word) for word in sent_packet)
    packet_size = len(sent_packet)

    sink_node_list.clear()
    packet_path = packets_dir + "/" + packet
    cmd = ["java", "-jar", java_app, packet_path]
    process = subprocess.Popen(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    out = process.communicate()
    print(out)
    check_for_exception = out[1].decode("utf-8")

    # check: did program run without exceptions?
    if check_for_exception.find("Exception") >= 0:
        # add to fail file: the failed packet, length of the sent packet, and the contents of the java log file.
        exception_fail_file.write(packet + ', {}, exception case\n{}'.format(sent_packet_len, check_for_exception))
        transfer_java_log(exception_fail_file)
        fail_count += 1
        exception_fail_count += 1
        clean_dir(os.getcwd())
        continue

    # no runtime errors occurred then proceed with checking the contents of output with the original packet data
    received_packet = open("t1-output.txt").read()
    comparison = difflib.SequenceMatcher(None, sent_packet, received_packet)
    block = comparison.get_matching_blocks()

    # check: does the java_app output match the source packet?
    if block[0].size == packet_size:
        # store successful packet transmission tests into success file.
        success_file.write(packet + "\n")
        pass_count += 1
        clean_dir(os.getcwd())
        continue
    else:
        # the packets did not match, store the result into fail file.
        decode_fail_file.write(packet + ', original packet length {}, match case\n'.format(sent_packet_len))

        # read the local encoding vectors from packet.log created by java
        packet_log_fname = packet + ".log"
        get_network_parameters(packet_log_fname)

        # identify the generation where decoding failure occurs
        decode_fail_gen = int(block[0].size / 100)

        # print the local encoding vectors used, and the received packets at the sink nodes to the fail file.
        transfer_java_log(decode_fail_file)

        # print the number of matching characters to fail file.
        decode_fail_file.write("received packet differs after character number {}\n\n".format(block[0].size))

        # print received and sent packets in alternating rows for easy comparison
        recvd = list(chunks(received_packet, 100))
        snt = list(chunks(sent_packet, 100))
        decode_fail_file.write("packet comparison: \n")
        for chunk_s, chunk_r in zip(snt, recvd):
            decode_fail_file.write("snt: " + chunk_s + '\n')
            decode_fail_file.write("rvd: " + chunk_r + '\n\n')
        fail_count += 1
        decode_fail_count += 1
        clean_dir(os.getcwd())
        continue

# move java logs into log directory
for file in glob.glob(r'*.log'):
    shutil.move(file, log_dir)

# print a score of test results.
print("fails: ", fail_count,
      "passes: ", pass_count, '\n\n',
      "exception fails: ", exception_fail_count,
      "decode errors: ", decode_fail_count)
