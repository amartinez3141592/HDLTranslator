module empty(
	input wire [2:0] a,
	input wire clk,
	input wire reset,
	output wire [2:0] led
);
	always @(( posedge clk or negedge reset )) begin
		if (!(reset)) begin
		end else begin
		end
	end
	always @(a) begin
		led = 3'b000;
	end
endmodule