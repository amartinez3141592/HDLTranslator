module empty(
	input wire [2:0] a,
	input wire VCC,
	input wire GND,
	input wire clk,
	input wire reset,
	output wire [2:0] led
);
	reg next_isActivated;
	reg isActivated;
	always @(posedge clk or negedge reset) begin
		if (!reset) begin
			isActivated <= 1'b0;
		end else begin
			isActivated <= next_isActivated;
		end
	end
	always @(a) begin
		next_isActivated = isActivated;
		led = 3'b000;
	end
endmodule