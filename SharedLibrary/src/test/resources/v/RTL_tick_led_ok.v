module tick_light(
	input wire clk,
	input wire reset,
	output wire red_led
);
	localparam
		S0 = 2'b10,
		S1 = 2'b01;
	reg [1:0] next_state;
	reg [1:0] state;
	always @(( posedge clk or negedge reset )) begin
		if (!(reset)) begin
			state <= S0;
		end else begin
			state <= next_state;
		end
	end
	always @(state) begin
		next_state = state;
		red_led = 1'b0;
		case(state)
			S0: begin
				red_led = 1;
				if (1) begin
					next_state = S1;
				end
			end
			S1: begin
				red_led = 0;
				if (1) begin
					next_state = S0;
				end
			end
		endcase
	end
endmodule