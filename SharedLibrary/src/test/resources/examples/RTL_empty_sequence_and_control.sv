module empty(
	input logic [2:0] a,
	input logic VCC,
	input logic GND,
	input logic clk,
	input logic reset,
	output logic [2:0] led
);
	logic next_isActivated;
	logic isActivated;
	always_ff @(posedge clk or negedge reset) begin
		if (!reset) begin
			isActivated <= 1'b0;
		end else begin
			isActivated <= next_isActivated;
		end
	end
	always_comb begin 
		next_isActivated = isActivated;
		led = 3'b000;
	end

endmodule

