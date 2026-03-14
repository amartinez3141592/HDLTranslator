module parallel_to_serial(
	input logic [2:0] parallel,
	input logic clk,
	input logic reset,
	output logic serial
);
	logic [2:0] aux;
	logic [2:0] next_aux;
	typedef enum logic [2:0] {
		S0 = 3'b100,
		S1 = 3'b010,
		S2 = 3'b001
	} step_t;
	step_t next_step;
	step_t step;
	always_ff @( posedge clk or negedge reset ) begin
		if (!(reset)) begin
			aux <= 3'b000;
			step <= S0;
		end else begin
			aux <= next_aux;
			step <= next_step;
		end
	end
	always_comb begin 
		next_step = step;
		next_aux = aux;
		serial = 1'b0;
		case(step)
			S0: begin
				next_aux={parallel[2],parallel[1],parallel[0]};
				serial=aux[0];
				if (1) begin next_step = S1;
				end
			end
			S1: begin
				next_aux={parallel[0],parallel[2],parallel[1]};
				serial=aux[0];
				if (1) begin next_step = S2;
				end
			end
			S2: begin
				next_aux={parallel[1],parallel[0],parallel[2]};
				serial=aux[0];
				if (1) begin next_step = S0;
				end
			end
		endcase
	end
endmodule
