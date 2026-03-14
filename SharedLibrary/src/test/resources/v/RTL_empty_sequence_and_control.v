module empty(
	input wire [2:0] a,
	input wire clk,
	input wire reset,
	output reg [2:0] led
);
	always @(posedge clk or negedge reset) begin
		if (!(reset)) begin
		end else begin
		end
	end
	always @(*) begin
		led = 3'b000;
	end
endmodule