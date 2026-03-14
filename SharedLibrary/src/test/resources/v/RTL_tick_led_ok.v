module tick_light(
	input wire clk,
	input wire reset,
	output reg red_led
);
	localparam
		S0 = 2'b10,
		S1 = 2'b01;
	reg [1:0] next_step;
	reg [1:0] step;
	always @(posedge clk or negedge reset) begin
		if (!(reset)) begin
			step <= S0;
		end else begin
			step <= next_step;
		end
	end
	always @(*) begin
		next_step = step;
		red_led = 1'b0;
		case(step)
			S0: begin
				red_led = 1;
				if (1) begin
					next_step = S1;
				end
			end
			S1: begin
				red_led = 0;
				if (1) begin
					next_step = S0;
				end
			end
		endcase
	end
endmodule