module tick_light(
	input logic clk,
	input logic reset,
	output logic red_led
);
	typedef enum logic [1:0] {
		S0 = 2'b10,
		S1 = 2'b01
	} step_t;
	step_t next_step;
	step_t step;
	always_ff @( posedge clk or negedge reset ) begin
		if (!(reset)) begin
			step <= S0;
		end else begin
			step <= next_step;
		end
	end
	always_comb begin 
		next_step = step;
		red_led = 1'b0;
		case(step)
			S0: begin
				red_led=1;
				if (1) begin next_step = S1;
				end
			end
			S1: begin
				red_led=0;
				if (1) begin next_step = S0;
				end
			end
		endcase
	end
endmodule
