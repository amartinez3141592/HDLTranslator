module tick_light(
	input logic clk,
	input logic reset,
	output logic red_led
);
	typedef enum logic [1:0] {
		S0 = 2'b10,
		S1 = 2'b01
	} state_t;
	state_t next_state;
	state_t state;
	always_ff @( posedge clk or negedge reset ) begin
		if (!(reset)) begin
			state <= S0;
		end else begin
			state <= next_state;
		end
	end
	always_comb begin 
		next_state = state;
		red_led = 1'b0;
		case(state)
			S0: begin
				red_led=1;
				if (1) begin next_state = S1;
				end
			end
			S1: begin
				red_led=0;
				if (1) begin next_state = S0;
				end
			end
		endcase
	end
endmodule
