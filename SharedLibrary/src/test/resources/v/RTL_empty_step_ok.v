module empty(
	input wire [2:0] a,
	input wire clk,
	input wire reset,
	output reg [2:0] led
);
	localparam
		S0 = 3'b100,
		S1 = 3'b010,
		S2 = 3'b001;
	reg [2:0] next_step;
	reg [2:0] step;
	always @(posedge clk or negedge reset) begin
		if (!(reset)) begin
			step <= S0;
		end else begin
			step <= next_step;
		end
	end
	always @(*) begin
		next_step = step;
		led = 3'b000;
		case(step)
			S0: begin
				if (1) begin
					next_step = S1;
				end
			end
			S1: begin
				led = {a[0],a[2],a[1]};
				if (1) begin
					next_step = S2;
				end
			end
			S2: begin
				led = {a[1],a[0],a[2]};
				if (1) begin
					next_step = S0;
				end
			end
		endcase
	end
endmodule