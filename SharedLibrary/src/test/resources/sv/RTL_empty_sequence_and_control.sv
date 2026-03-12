module empty(
	input logic [2:0] a,
	input logic clk,
	input logic reset,
	output logic [2:0] led
);
	always_ff @( posedge clk or negedge reset ) begin
		if (!(reset)) begin
		end else begin
		end
	end
	always_comb begin 
		led = 3'b000;
	end
endmodule
